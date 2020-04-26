package org.oagi.srt.gateway.http.api.info.service;

import org.oagi.srt.data.BieState;
import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.bie_management.service.BieRepository;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContext;
import org.oagi.srt.gateway.http.api.context_management.data.FindBizCtxIdsByTopLevelAbieIdsResult;
import org.oagi.srt.gateway.http.api.context_management.repository.BusinessContextRepository;
import org.oagi.srt.gateway.http.api.info.data.SummaryBie;
import org.oagi.srt.gateway.http.api.info.data.SummaryBieInfo;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BieInfoService {

    @Autowired
    private BieRepository repository;

    @Autowired
    private BusinessContextRepository bizCtxRepository;

    @Autowired
    private SessionService sessionService;

    public SummaryBieInfo getSummaryBieInfo(User user) {
        if (user == null) {
            throw new DataAccessForbiddenException("Need authentication to access information.");
        }

        List<SummaryBie> summaryBieList = repository.getSummaryBieList();
        long requesterId = sessionService.userId(user);

        SummaryBieInfo info = new SummaryBieInfo();
        Map<BieState, Integer> numberOfTotalBieByStates =
                summaryBieList.stream().collect(Collectors.toMap(SummaryBie::getState, (e) -> 1, Integer::sum));
        info.setNumberOfTotalBieByStates(numberOfTotalBieByStates);

        Map<BieState, Integer> numberOfMyBieByStates =
                summaryBieList.stream()
                        .filter(e -> e.getOwnerUserId() == requesterId)
                        .collect(Collectors.toMap(SummaryBie::getState, (e) -> 1, Integer::sum));
        info.setNumberOfMyBieByStates(numberOfMyBieByStates);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MONTH, -1);
        Date dayBeforeMonth = calendar.getTime();

        List<SummaryBie> recentlyWorkedOn = summaryBieList.stream()
                .filter(e -> e.getOwnerUserId() == requesterId)
                .filter(e -> e.getLastUpdateTimestamp().after(dayBeforeMonth))
                .sorted(Comparator.comparing(SummaryBie::getLastUpdateTimestamp).reversed())
                .limit(10)
                .collect(Collectors.toList());

        Map<Long, List<FindBizCtxIdsByTopLevelAbieIdsResult>> res = bizCtxRepository.findBizCtxIdsByTopLevelAbieIds(
                recentlyWorkedOn.stream().map(e -> e.getTopLevelAbieId()).collect(Collectors.toList())
        );
        recentlyWorkedOn.forEach(e -> {
            e.setBusinessContexts(res.get(e.getTopLevelAbieId()).stream().map(item -> {
                BusinessContext bc = new BusinessContext();
                bc.setBizCtxId(item.getBizCtxId());
                bc.setName(item.getName());
                return bc;
            }).collect(Collectors.toList()));
        });

        info.setRecentlyWorkedOn(recentlyWorkedOn);

        return info;
    }
}
