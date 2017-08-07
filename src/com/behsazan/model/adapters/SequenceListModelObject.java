package com.behsazan.model.adapters;

import com.behsazan.model.entity.Sequence;

/**
 * Created by admin on 08/06/2017.
 */
public class SequenceListModelObject {
    private Sequence sequence;

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
}
