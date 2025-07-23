return new Date(userTime - offset);
    }

    /**
     * 将UTC时间转换为本地时间
     */
    public void convertUTCToLocalTime(Issue issue, String userTimezone) {
        convertIssueTimeToUserTZ(issue, userTimezone);
    }
}