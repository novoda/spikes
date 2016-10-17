import { Routes, RouterModule } from '@angular/router';
import { ContributorsDashboardComponent } from './contributors-dashboard/contributors-dashboard.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { DashboardCarouselComponent } from './dashboard-carousel/dashboard-carousel.component';
import { SonarCoverageComponent } from './dashboards/sonar-coverage/sonar-coverage.component';

const appRoutes: Routes = [
  {path: 'work', component: ContributorsDashboardComponent},
  {path: 'dashboards', component: DashboardCarouselComponent},
  {path: 'dashboards/sonar-coverage', component: SonarCoverageComponent},
  {path: '**', component: PageNotFoundComponent}
];

export const routing = RouterModule.forRoot(appRoutes);
