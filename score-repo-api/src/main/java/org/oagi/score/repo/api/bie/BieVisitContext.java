package org.oagi.score.repo.api.bie;

import org.oagi.score.repo.api.bie.model.*;

import java.util.Collection;

public interface BieVisitContext {

    Asbiep getAsbiep(TopLevelAsbiep topLevelAsbiep);

    Abie getRoleOfAbie(Asbiep asbiep);

    Collection<? extends BieAssociation> getAssociations(Abie abie);

    Asbiep getToAsbiep(Asbie asbie);

    Bbiep getToBbiep(Bbie bbie);

}
