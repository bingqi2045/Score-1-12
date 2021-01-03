package org.oagi.score.service.bie;

import org.oagi.score.repo.api.bie.model.*;
import org.oagi.score.service.corecomponent.CcDocument;

import java.util.Collection;
import java.util.List;

public interface BieDocument {

    Asbiep getRootAsbiep();

    Abie getAbie(Asbiep asbiep);

    Collection<BieAssociation> getAssociations(Abie abie);

    Asbiep getAsbiep(Asbie asbie);

    Bbiep getBbiep(Bbie bbie);

    List<BbieSc> getBbieScList(Bbie bbie);

    CcDocument getCcDocument();

    void accept(BieVisitor visitor);

}
