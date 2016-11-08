import { Injectable } from '@angular/core';
import { ReportsService } from './reports.service';
import { Observable } from 'rxjs';
import { UserStats } from './user-stats';
import { CompanyStats } from './company-stats';

@Injectable()
export class ReportsClient {

  constructor(private reportsService: ReportsService) {
  }

  getCompanyStats(from: Date, to: Date, timezone: string): Observable<CompanyStats> {

    return this.reportsService
      .getAggregatedStats(from, to, timezone)
      .mergeMap((stats: {usersStats: any}) => {
        return Observable
          .from(Object
            .keys(stats.usersStats)
            .map(this.toUserStats(stats))
          );
      })
      .filter(this.excludeUsersWithNoAssignedContributions)
      .toArray()
      .map(this.into);
  }

  private into(users: Array<UserStats>): CompanyStats {
    const contributors: Array<UserStats> = users.filter((userStats: UserStats) => {
      return userStats.externalCount > 0;
    });
    const slackers: Array<UserStats> = users.filter((userStats: UserStats) => {
      return userStats.externalCount <= 0;
    });
    return new CompanyStats(contributors, slackers);
  }

  private toUserStats(stats: {usersStats: any}) {
    return (key: string) => {
      const userStats = stats.usersStats[key];
      let projects = Object.keys(userStats.assignedProjectsStats || {});
      if (projects.length === 0) {
        projects = ['No assignment'];
      }
      const normalizedProjects = this.normaliseProjects(projects);
      const allProjects = this.removeDuplicates(normalizedProjects);
      return new UserStats(
        key,
        allProjects,
        userStats.assignedProjectsContributions,
        userStats.externalRepositoriesContributions || 0
      );
    };
  }

  private normaliseProjects(projects: string[]): string[] {
    return projects
      .map(project => {
        return project.replace(/(: (Scheduled|Verified)$)/g, '');
      });
  }

  private removeDuplicates(normalizedProjects: string[]): string {
    return Array
      .from(new Set(normalizedProjects))
      .join(', ');
  }

  private excludeUsersWithNoAssignedContributions(userStats: UserStats): boolean {
    return (userStats.assignedCount || 0) > 0;
  }

}
