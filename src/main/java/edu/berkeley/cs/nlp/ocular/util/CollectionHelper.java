package edu.berkeley.cs.nlp.ocular.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionHelper {

	public static <K, V> Map<K, V> map1(K key, V value) {
		return Collections.singletonMap(key, value);
	}

	public static <K, V> Map<K, V> makeMap(Tuple2<K, V>... tuples) {
		if (tuples.length == 0) {
			return Collections.<K, V> emptyMap();
		}
		else if (tuples.length == 1) {
			Tuple2<K, V> p = tuples[0];
			return Collections.singletonMap(p._1, p._2);
		}
		else {
			Map<K, V> m = new HashMap<K, V>();
			for (Tuple2<K, V> tuple : tuples) {
				m.put(tuple._1, tuple._2);
			}
			return m;
		}
	}

	public static <K, V> V getOrElse(Map<K, V> m, K k, V def) {
		V v = m.get(k);
		if (v != null)
			return v;
		else
			return def;
	}

	public static <A> Set<A> setUnion(Set<A>... sets) {
		if (sets.length == 0)
			return Collections.<A> emptySet();
		else if (sets.length == 1)
			return sets[0];
		else {
			Set<A> set = new HashSet<A>();
			for (Set<A> xs : sets)
				set.addAll(xs);
			return set;
		}
	}

	public static <A> Set<A> setDiff(Set<A> a, Set<A> b) {
		Set<A> set = new HashSet<A>();
		for (A x : a)
			if (!b.contains(x)) 
				set.add(x);
		return set;
	}

	public static <A> Set<A> setIntersection(Set<A> a, Set<A> b) {
		Set<A> set = new HashSet<A>();
		for (A x : a)
			if (b.contains(x)) 
				set.add(x);
		return set;
	}

	public static <A> List<A> makeList(Collection<? extends A> xs) {
		if (xs.size() == 0)
			return Collections.<A> emptyList();
		else {
			List<A> l = new ArrayList<A>();
			l.addAll(xs);
			return l;
		}
	}
	
	public static <A> List<A> makeList(A... xs) {
		if (xs.length == 0)
			return Collections.<A> emptyList();
		else {
			List<A> l = new ArrayList<A>();
			Collections.addAll(l, xs);
			return l;
		}
	}
	
	public static <A> List<A> fillList(int size, A item) {
		List<A> l = new ArrayList<A>(size);
		for (int i = 0; i < size; ++i)
			l.add(item);
		return l;
	}
	
	public static <A> Set<A> makeSet(Collection<A> xs) {
		if (xs.size() == 0)
			return Collections.<A> emptySet();
		else if (xs.size() == 1)
			return Collections.singleton(xs.iterator().next());
		else {
			Set<A> set = new HashSet<A>(xs.size());
			set.addAll(xs);
			return set;
		}
	}
	
	public static <A> Set<A> makeSet(A... xs) {
		if (xs.length == 0)
			return Collections.<A> emptySet();
		else if (xs.length == 1)
			return Collections.singleton(xs[0]);
		else {
			Set<A> set = new HashSet<A>(xs.length);
			Collections.addAll(set, xs);
			return set;
		}
	}

	public static <A> List<A> listCat(List<A>... lists) {
		if (lists.length == 0)
			return Collections.<A> emptyList();
		else if (lists.length == 1)
			return lists[0];
		else {
			List<A> full = new ArrayList<A>();
			for (List<A> xs : lists)
				full.addAll(xs);
			return full;
		}
	}

	public static <A> Iterator<List<A>> sliding(List<A> list, int n) {
		return new SlidingIterator<A>(list, n);
	}

	private static class SlidingIterator<A> implements Iterator<List<A>> {
		private List<A> list;
		private int n;
		private int position;

		public SlidingIterator(List<A> list, int n) {
			if (n <= 0)
				throw new IllegalArgumentException("`n` must be greater than zero");
			this.list = list;
			this.n = n;
			this.position = 0;
		}

		public boolean hasNext() {
			return position < list.size() - n + 1;
		}

		public List<A> next() {
			List<A> result = new ArrayList<A>();
			for (int j = 0; j < n; ++j)
				result.add(list.get(position + j));
			++position;
			return result;
		}

		public void remove() {
			throw new RuntimeException("remove not supported on SlidingIterator");
		}
	}

	public static <A> List<A> take(List<A> list, int n) {
		List<A> result = new ArrayList<A>();
		for (int j = 0; j < Math.min(n, list.size()); ++j)
			result.add(list.get(j));
		return result;
	}

	public static <A> List<A> takeRight(List<A> list, int n) {
		List<A> result = new ArrayList<A>();
		for (int j = list.size() - n; j < list.size(); ++j)
			result.add(list.get(j));
		return result;
	}

	public static List<Integer> intArrayToList(int[] a) {
		List<Integer> l = new ArrayList<Integer>(a.length);
		for (int x: a) l.add(x);
		return l;
	}
	
	public static int[] intListToArray(List<Integer> l) {
		int[] a = new int[l.size()];
		for (int i = 0; i < a.length; i++) {
			a[i] = l.get(i);
		}
		return a;
	}
	
	public static <A,B> int longestCommonPrefix(List<A> as, List<B> bs) {
		int longestCommonPrefix = 0;
		int aLen = as.size();
		int bLen = bs.size();
		while (longestCommonPrefix < aLen && longestCommonPrefix < bLen && as.get(longestCommonPrefix).equals(bs.get(longestCommonPrefix))) 
			++longestCommonPrefix;
		return longestCommonPrefix;
	}
	
}
