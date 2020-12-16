package de.fh.stud.p3;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.fh.stud.p1.Knoten;
import de.fh.stud.p3.Suche.Node;

public abstract class ClosedListSerach<TNode extends Node, TQueue extends Queue<TNode>> {

	protected abstract TQueue create(Stream<TNode> initialNodes);

	protected abstract void insert(TQueue ref, Stream<TNode> nodes);

	@SuppressWarnings("unchecked")
	public TNode closedListSerach(TNode initialNode, Predicate<TNode> goalTest) {

		long start = System.currentTimeMillis();
		long lastMessage = System.currentTimeMillis();

		System.out.println("    time     #open   #closed   used mb   mem%");

		Set<TNode> closedList = new HashSet<>(10_000_000, 0.9F);
		TQueue openList = create(Stream.of(initialNode));

		BiConsumer<TQueue, Set<TNode>> printStatus = (pOpenList, pClosedList) -> {
			Runtime rt = Runtime.getRuntime();

			long maxMB = rt.maxMemory() / 1024 / 1024;
			long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
			long percent = (long) (((double) usedMB / (double) maxMB) * 100D);

			String log = String.format("%7ds   %7d   %7d   %7d   %3d%%", (System.currentTimeMillis() - start) / 1000,
					pOpenList.size(), pClosedList.size(), usedMB, percent);
			System.out.println(log);
		};

		while (!openList.isEmpty()) {

			long diff = System.currentTimeMillis() - lastMessage;
			if (diff >= 1000) {
				printStatus.accept(openList, closedList);
				lastMessage = System.currentTimeMillis();
			}

			TNode node = openList.remove();

			if (goalTest.test(node)) {
				printStatus.accept(openList, closedList);
				System.out.println("Fininshed: " + openList.size() + "\t" + closedList.size() + "\t"
						+ ((Knoten) node).getResultedByActions().size());
				return node;
			}

			boolean nodeWasSeenBefore = !closedList.add(node);
			if (nodeWasSeenBefore) {
				continue;
			}

			insert(openList, (Stream<TNode>) node.expand().filter(x -> !closedList.contains(x)));
		}

		// return failure
		return null;
	}
}
