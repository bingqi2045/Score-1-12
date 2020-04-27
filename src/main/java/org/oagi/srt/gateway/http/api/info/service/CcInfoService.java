package org.oagi.srt.gateway.http.api.info.service;

import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.bie_management.service.BieRepository;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.repository.CoreComponentRepository;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContext;
import org.oagi.srt.gateway.http.api.context_management.data.FindBizCtxIdsByTopLevelAbieIdsResult;
import org.oagi.srt.gateway.http.api.context_management.repository.BusinessContextRepository;
import org.oagi.srt.gateway.http.api.info.data.SummaryBieForCcExt;
import org.oagi.srt.gateway.http.api.info.data.SummaryCcExt;
import org.oagi.srt.gateway.http.api.info.data.SummaryCcExtInfo;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class CcInfoService {

    @Autowired
    private CoreComponentRepository ccRepository;

    @Autowired
    private BieRepository bieRepository;

    @Autowired
    private BusinessContextRepository bizCtxRepository;

    @Autowired
    private SessionService sessionService;

    public SummaryCcExtInfo getSummaryCcExtInfo(User user) {
        if (user == null) {
            throw new DataAccessForbiddenException("Need authentication to access information.");
        }

        List<SummaryCcExt> summaryCcExtList = ccRepository.getSummaryCcExtList();
        long requesterId = sessionService.userId(user);

        SummaryCcExtInfo info = new SummaryCcExtInfo();
        Map<CcState, Integer> numberOfCcExtByStates =
                summaryCcExtList.stream().collect(Collectors.toMap(SummaryCcExt::getState, (e) -> 1, Integer::sum));
        info.setNumberOfTotalCcExtByStates(numberOfCcExtByStates);

        Map<CcState, Integer> numberOfUegByUsers =
                summaryCcExtList.stream()
                        .filter(e -> e.getOwnerUserId() == requesterId)
                        .collect(Collectors.toMap(SummaryCcExt::getState, (e) -> 1, Integer::sum));
        info.setNumberOfMyCcExtByStates(numberOfUegByUsers);

        List<SummaryBieForCcExt> summaryBieList = bieRepository.getSummaryBieListByAccIds(
                summaryCcExtList.stream().map(e -> e.getAccId()).collect(Collectors.toList())
        );

        Map<Long, List<FindBizCtxIdsByTopLevelAbieIdsResult>> res = bizCtxRepository.findBizCtxIdsByTopLevelAbieIds(
                summaryBieList.stream().map(e -> e.getTopLevelAbieId()).collect(Collectors.toList())
        );
        summaryBieList.forEach(e -> {
            e.setBusinessContexts(res.get(e.getTopLevelAbieId()).stream().map(item -> {
                BusinessContext bc = new BusinessContext();
                bc.setBizCtxId(item.getBizCtxId());
                bc.setName(item.getName());
                return bc;
            }).collect(Collectors.toList()));
        });

        Map<String, List<SummaryBieForCcExt>> summaryCcExtListMap = summaryBieList.stream()
                .collect(groupingBy(SummaryBieForCcExt::getObjectClassTerm));
        info.setSummaryCcExtListMap(summaryCcExtListMap);

        return info;
    }
}
