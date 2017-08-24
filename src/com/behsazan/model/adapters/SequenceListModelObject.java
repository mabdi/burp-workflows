package com.behsazan.model.adapters;

import com.behsazan.model.entity.Sequence;
import com.behsazan.model.entity.Flow_Sequence;

/**
 * Created by admin on 08/06/2017.
 */
public class SequenceListModelObject {
    private Sequence sequence;
    private Flow_Sequence flow_sequence;

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

    public Flow_Sequence getFlow_sequence() {
        return flow_sequence;
    }

    public void setFlow_sequence(Flow_Sequence flow_sequence) {
        this.flow_sequence = flow_sequence;
    }

}
