package org.oagi.srt.gateway.http.api.info.service;

import org.oagi.srt.gateway.http.api.bie_management.service.BieRepository;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.repository.CoreComponentRepository;
import org.oagi.srt.gateway.http.api.context_management.repository.BusinessContextRepository;
import org.oagi.srt.gateway.http.api.info.data.SummaryBieForCcExt;
import org.oagi.srt.gateway.http.api.info.data.SummaryCcExt;
import org.oagi.srt.gateway.http.api.info.data.SummaryCcExtInfo;
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

    public SummaryCcExtInfo getSummaryCcExtInfo(User user) {
        List<SummaryCcExt> summaryCcExtList = ccRepository.getSummaryCcExtList();

        SummaryCcExtInfo info = new SummaryCcExtInfo();
        Map<CcState, Integer> numberOfCcExtByStates =
                summaryCcExtList.stream().collect(Collectors.toMap(SummaryCcExt::getState, (e) -> 1, Integer::sum));
        info.setNumberOfUegByStates(numberOfCcExtByStates);

        Map<String, Integer> numberOfUegByUsers =
                summaryCcExtList.stream().collect(Collectors.toMap(SummaryCcExt::getOwnerUsername, (e) -> 1, Integer::sum));
        info.setNumberOfUegByUsers(numberOfUegByUsers);

        List<SummaryBieForCcExt> summaryBieList = bieRepository.getSummaryBieListByAccIds(
                summaryCcExtList.stream().map(e -> e.getAccId()).collect(Collectors.toList())
        );
        summaryBieList.forEach(e -> {
            List<Long> bizCtxIds = bieRepository.getBizCtxIdByTopLevelAbieId(e.getTopLevelAbieId());
            e.setBusinessContexts(bizCtxRepository.findBusinessContextsByBizCtxIdIn(bizCtxIds));
        });

        Map<Long, List<SummaryBieForCcExt>> summaryCcExtListMap = summaryBieList.stream()
                .collect(groupingBy(SummaryBieForCcExt::getAccId));
        info.setSummaryCcExtListMap(summaryCcExtListMap);

        return info;
    }
}
