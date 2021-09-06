package com.db.edu.storage;

import com.db.edu.dao.Discussion;

import java.util.List;

public class DiscussionStorage {
    static List<Discussion> discussions;

    public static Discussion getDiscussionById(int discussionId) {
        for (Discussion discussion : discussions) {
            if (discussion.getId() == discussionId) {
                return discussion;
            }
        }
        throw new RuntimeException("Discussion doesn't exist");
    }
}