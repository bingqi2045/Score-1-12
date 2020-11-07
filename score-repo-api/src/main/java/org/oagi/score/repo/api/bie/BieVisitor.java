package org.oagi.score.repo.api.bie;

import org.oagi.score.repo.api.bie.model.*;

public interface BieVisitor {

    void visitStart(TopLevelAsbiep topLevelAsbiep, BieVisitContext context);

    void visitEnd(TopLevelAsbiep topLevelAsbiep, BieVisitContext context);

    void visitAbie(Abie abie, BieVisitContext context);

    void visitAsbie(Asbie asbie, BieVisitContext context);

    void visitBbie(Bbie bbie, BieVisitContext context);

    void visitAsbiep(Asbiep asbiep, BieVisitContext context);

    void visitBbiep(Bbiep bbiep, BieVisitContext context);

    void visitBbieSc(BbieSc bbieSc, BieVisitContext context);

}
