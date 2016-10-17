import { Injectable } from '@angular/core';
import { Http, URLSearchParams, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs';
import { ConfigService } from '../../config.service';
import { SonarDetails } from './SonarDetails';

@Injectable()
export class SonarService {

  private static API_PROJECTS_INDEX = 'projects/index';

  constructor(private http: Http, private config: ConfigService) {
  }

  getProjects(): Observable<() => string> {
    return this.config
      .getSonarDetails()
      .flatMap((sonarDetails: SonarDetails) => {
        let head = new Headers();
        head.append("Authorization", "Basic " + btoa(sonarDetails.token + ":"));
        head.append("Content-Type", "application/json");
        head.append("Accept", "application/json");
        console.log(head);
        console.log(sonarDetails.api_url + SonarService.API_PROJECTS_INDEX);
        return this.http.get(sonarDetails.api_url + SonarService.API_PROJECTS_INDEX, {headers: head});
      })
      .map((res: Response) => {
        return res.json();
      });
  }

}
