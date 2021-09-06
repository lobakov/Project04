package com.db.edu.server.storage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DiscussionStorage {
    static Map<Integer, Set<Integer>> discussions;

    public static Set<Integer> getUsersById(int discussionId) {
        if (discussions.get(discussionId) == null) {
            throw new RuntimeException("Discussion doesn't exist");
        }
        return discussions.get(discussionId);
    }

    public static void addUserToDiscussion(int userId, int discussionId) {
        discussions.putIfAbsent(discussionId, new HashSet<>());
        discussions.get(discussionId).add(userId);
    }
}
