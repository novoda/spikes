import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { ContributorsDashboardComponent } from './contributors-dashboard/contributors-dashboard.component';
import { TeamContributorComponent } from './team-contributor/team-contributor.component';
import { ReportsClient } from './reports/reports-client.service';
import { WeekCalculator } from './week-calculator.service';
import { SystemClock } from './system-clock';
import { routing } from './app.routes';
import { HttpModule } from '@angular/http';
import { reportsServiceProvider } from './reports/reports.service.provider';
import { TeamContributorsComponent } from './team-contributors/team-contributors.component';
import { ProjectContributorComponent } from './project-contributor/project-contributor.component';
import { ProjectContributorsComponent } from './project-contributors/project-contributors.component';
import { ConfigService } from './config.service';
import { TimezoneDetectorService } from './timezone-detector.service';
import { DashboardCarouselComponent } from './dashboard-carousel/dashboard-carousel.component';
import { SonarCoverageComponent } from './dashboards/sonar-coverage/sonar-coverage.component';
import { SonarService } from './dashboards/sonar-coverage/sonar.service';

@NgModule({
  imports: [
    BrowserModule,
    CommonModule,
    FormsModule,
    HttpModule,
    routing
  ],
  declarations: [
    AppComponent,
    PageNotFoundComponent,
    ContributorsDashboardComponent,
    TeamContributorsComponent,
    ProjectContributorsComponent,
    TeamContributorComponent,
    ProjectContributorComponent,
    DashboardCarouselComponent,
    SonarCoverageComponent
  ],
  providers: [
    SystemClock,
    WeekCalculator,
    ConfigService,
    reportsServiceProvider,
    ReportsClient,
    TimezoneDetectorService,
    SonarService
  ],
  entryComponents: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule {

}
