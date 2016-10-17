import { ConfigService } from '../../config.service';
import { SonarDetails } from './SonarDetails';
import { Injectable, Component } from '@angular/core';
import { Headers, Http, Response, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs';
import { Project } from './Project';
import { Coverage } from './Coverage';

@Injectable()
export class SonarService {

  private static API_PROJECTS_INDEX = 'projects/index';
  private static API_MEASURES_COMPONENT = 'measures/component';

  constructor(private http: Http, private config: ConfigService) {
  }

  getProjects(): Observable<Array<Coverage>> {
    let config: Observable<SonarDetails> = this.config.getSonarDetails().cache();
    let eachProject: Observable<Project> = config.flatMap(this.fetchProjects())
      .flatMap(this.parseEachProject());
    let eachProjectCoverage: Observable<Array<Coverage>> = config.combineLatest(eachProject,
      (sonarDetails: SonarDetails, project: Project): Observable<Coverage> => {
        return this.fetchCoverage(sonarDetails, project)
          .flatMap(this.parseCoverage());
      }
    ).concatAll().toArray();
    return eachProjectCoverage;
  }

  fetchProjects(): (SonarDetails) => Observable<Response> {
    return (sonarDetails: SonarDetails): Observable<Response> => {
      let headers = new Headers({ "Authorization": "Basic " + btoa(sonarDetails.token + ":") });
      let params: URLSearchParams = new URLSearchParams();
      params.set('format', 'json');
      let url = sonarDetails.api_url + SonarService.API_PROJECTS_INDEX;
      return this.http.get(url, { headers: headers, search: params });
    }
  }

  parseEachProject(): (Response) => Observable<Project> {
    return (res: Response): Observable<Project> => {
      let projects: Array<Project> = res.json().map((item) => {
        return new Project(item.nm, item.k);
      });
      return Observable.from(projects);
    };
  }

  fetchCoverage(sonarDetails: SonarDetails, project: Project): Observable<Response> {
    let headers = new Headers({ "Authorization": "Basic " + btoa(sonarDetails.token + ":") });
    let params: URLSearchParams = new URLSearchParams();
    params.set('format', 'json');
    params.set('componentKey', project.key);
    params.set('metricKeys', 'coverage');
    let url = sonarDetails.api_url + SonarService.API_MEASURES_COMPONENT;
    return this.http.get(url, { headers: headers, search: params });
  }

  parseCoverage(): (Response) => Observable<Coverage> {
    return (res: Response): Observable<Coverage> => {
      let measures = res.json().component.measures;
      let name = res.json().component.name;
      if (measures && measures.length > 0) {
        return Observable.of(new Coverage(name, measures[0].value));
      } else {
        return Observable.from([]);
      }
    }
  }

}
