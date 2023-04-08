package com.georgeneokq.game.question;

public class QuestionFilter {

    private QuestionFilterPredicate predicate;
    private int retrieveLimit;

    public QuestionFilter(QuestionFilterPredicate predicate) {
        this(predicate, 0);
    }

    public QuestionFilter(QuestionFilterPredicate predicate, int retrieveLimit) {
        this.predicate = predicate;
        this.retrieveLimit = retrieveLimit;
    }

    public QuestionFilterPredicate getPredicate() {
        return predicate;
    }

    public int getRetrieveLimit() {
        return retrieveLimit;
    }
}
