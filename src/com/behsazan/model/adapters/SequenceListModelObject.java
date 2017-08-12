package com.behsazan.model.adapters;

import com.behsazan.model.entity.Sequence;
import com.behsazan.model.entity.TestCase_Sequence;

/**
 * Created by admin on 08/06/2017.
 */
public class SequenceListModelObject {
    private Sequence sequence;
    private TestCase_Sequence testCase_sequence;

    public SequenceListModelObject(Sequence seq) {
        this.sequence = seq;
    }


    public Sequence getSequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return sequence.toString();
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    public TestCase_Sequence getTestCase_sequence() {
        return testCase_sequence;
    }

    public void setTestCase_sequence(TestCase_Sequence testCase_sequence) {
        this.testCase_sequence = testCase_sequence;
    }

}
