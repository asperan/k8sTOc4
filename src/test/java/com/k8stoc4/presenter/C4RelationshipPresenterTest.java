package com.k8stoc4.presenter;

import com.k8stoc4.model.C4Relationship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class C4RelationshipPresenterTest {

    @Test
    void testRelationshipWithoutTag() {
        final C4Relationship relationship = new C4Relationship("source", "target", "", "");
        final String expected = "source -> target\n";
        assertEquals(expected, C4RelationshipPresenter.present(relationship));
    }

    @Test
    void testRelationshipWithTag() {
        final C4Relationship relationship = new C4Relationship("source", "target", "", "", "tag");
        final String expected = "source -> target {\n    #tag\n}\n";
        assertEquals(expected, C4RelationshipPresenter.present(relationship));
    }
}
