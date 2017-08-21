package com.randioo.mahjong_public_server.comparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CardComparator implements Comparator<Integer> {

    // 白搭
    private Set<Integer> baidaCardSet = new HashSet<>();

    public Set<Integer> getBaidaCardSet() {
        return baidaCardSet;
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        if (baidaCardSet.size() > 0) {
            if (o1 == o2 && baidaCardSet.contains(o1)) {
                return 0;
            } else if (o1 != o2 && baidaCardSet.contains(o1) && baidaCardSet.contains(o2)) {
                return o1 - o2;
            } else if (baidaCardSet.contains(o1)) {
                return -1;
            } else if (baidaCardSet.contains(o2)) {
                return 1;
            }
        }
        return o1 - o2;
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(101);
        list.add(101);
        list.add(102);
        list.add(103);
        list.add(103);
        list.add(103);
        list.add(1103);
        list.add(801);

        CardComparator comparator = new CardComparator();
        comparator.getBaidaCardSet().add(801);

        Collections.sort(list, comparator);
        System.out.println(list);
    }
}
