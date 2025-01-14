import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {BieList, BieListRequest, SummaryBieInfo} from './bie-list';
import {PageResponse} from '../../../basis/basis';
import {BusinessContext} from '../../../context-management/business-context/domain/business-context';

@Injectable()
export class BieListService {
  constructor(private http: HttpClient) {
  }

  getSummaryBieList(releaseId: number): Observable<SummaryBieInfo> {
    return this.http.get<SummaryBieInfo>('/api/info/bie_summary?releaseId=' + releaseId).pipe(map(
      e => {
        if (e.myRecentBIEs) {
          e.myRecentBIEs = e.myRecentBIEs.map(elm => {
            elm.lastUpdateTimestamp = new Date(elm.lastUpdateTimestamp);
            return elm;
          });
        }
        return e;
      }));
  }

  getBieListWithRequest(request: BieListRequest): Observable<PageResponse<BieList>> {
    let params = new HttpParams()
      .set('sortActive', request.page.sortActive)
      .set('sortDirection', request.page.sortDirection)
      .set('pageIndex', '' + request.page.pageIndex)
      .set('pageSize', '' + request.page.pageSize);
    if (request.ownerLoginIds.length > 0) {
      params = params.set('ownerLoginIds', request.ownerLoginIds.join(','));
    }
    if (request.updaterLoginIds.length > 0) {
      params = params.set('updaterLoginIds', request.updaterLoginIds.join(','));
    }
    if (request.updatedDate.start) {
      params = params.set('updateStart', '' + request.updatedDate.start.getTime());
    }
    if (request.updatedDate.end) {
      params = params.set('updateEnd', '' + request.updatedDate.end.getTime());
    }
    if (request.filters.den) {
      params = params.set('den', request.filters.den);
    }
    if (request.filters.propertyTerm) {
      params = params.set('propertyTerm', request.filters.propertyTerm);
    }
    if (request.filters.businessContext) {
      params = params.set('businessContext', request.filters.businessContext);
    }
    if (request.filters.asccpManifestId) {
      params = params.set('asccpManifestId', '' + request.filters.asccpManifestId);
    }
    if (request.states.length > 0) {
      params = params.set('states', request.states.join(','));
    }
    if (request.access) {
      params = params.set('access', request.access);
    }
    if (request.excludePropertyTerms.length > 0) {
      params = params.set('excludePropertyTerms', request.excludePropertyTerms.join(','));
    }
    if (request.excludeTopLevelAsbiepIds.length > 0) {
      params = params.set('excludeTopLevelAsbiepIds', request.excludeTopLevelAsbiepIds.join(','));
    }
    if (request.release) {
      params = params.set('releaseId', request.release.releaseId.toString());
    }
    if (request.ownedByDeveloper !== undefined) {
      params = params.set('ownedByDeveloper', request.ownedByDeveloper.toString());
    }
    return this.http.get<PageResponse<BieList>>('/api/bie_list', {params: params});
  }

  findBizCtxFromAbieId(id): Observable<BusinessContext> {
    return this.http.get<BusinessContext>('/api/profile_bie/business_ctx_from_abie/' + id);
  }

  getBieUsageList(request: BieListRequest, topLevelAsbiepId: number): Observable<PageResponse<BieList>> {
    let params = new HttpParams()
      .set('sortActive', request.page.sortActive)
      .set('sortDirection', request.page.sortDirection)
      .set('pageIndex', '' + request.page.pageIndex)
      .set('pageSize', '' + request.page.pageSize);

    return this.http.get<PageResponse<BieList>>('/api/bie_list/' + topLevelAsbiepId + '/usage', {params: params});
  }

  getBieListByBizCtxId(id): Observable<BieList[]> {
    return this.http.get<BieList[]>('/api/profile_bie_list?biz_ctx_id=' + id);
  }

  delete(topLevelAsbiepIds: number[]): Observable<any> {
    return this.http.post<any>('/api/profile_bie_list/delete', {
      topLevelAsbiepIds: topLevelAsbiepIds
    });
  }

  transferOwnership(topLevelAsbiepIds: number, targetLoginId: string): Observable<any> {
    return this.http.post<any>('/api/profile_bie/' + topLevelAsbiepIds + '/transfer_ownership', {
      topLevelAsbiepIds: topLevelAsbiepIds,
      targetLoginId
    });
  }
}
