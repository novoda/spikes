import { Injectable } from '@angular/core';
import { Http, URLSearchParams, Response } from '@angular/http';
import { Observable } from 'rxjs';
import { ConfigService } from '../../../config.service';

@Injectable()
export class ReportsService {

  private static API_STATS_AGGREGATED = 'stats/aggregated';

  constructor(private http: Http, private config: ConfigService) {
  }

  getAggregatedStats(from: Date, to: Date, timezone: string): Observable<{usersStats: any}> {
    let params = new URLSearchParams();
    params.set('from', from.toISOString());
    params.set('to', to.toISOString());
    params.set('timezone', timezone);

    return this.config
      .getApiBase()
      .flatMap((apiBase: string) => {
        return this.http
          .get(apiBase + ReportsService.API_STATS_AGGREGATED, {
            search: params
          });
      })
      .map((res: Response) => {
        return res.json();
      });
  }

}
