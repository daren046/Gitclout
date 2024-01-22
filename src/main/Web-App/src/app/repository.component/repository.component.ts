import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {GitLinkService} from "../service/git-link.service";
import {ThemeService} from "../service/theme.service";

import {AccumulatedContribution, Contribution, Tag} from "../model/repository.model";
import {Chart, registerables} from 'chart.js';

Chart.register(...registerables);
@Component({
  selector: 'app-repository',
  templateUrl: './repository.component.html',
  styleUrls: ['./repository.component.css']
})
/**
 * This component is used to display the repository
 * it's second page of the application
 * @author: Tagnan Tremellat
 */
export class RepositoryComponent {
  repository: any;
  chartLabels: string[][] = []
  chartData: any[] = [];
  isDarkMode: boolean;
  numberOfTags: number = 1;
  actualTag: Tag = {id: -99, name: "", sha1: ""};
  selectedLanguage: string = '';
  chartLanguages: string[] = [];
  showInfoBubble: boolean = false;
  radarData: any;
  contribution: Contribution[] = [];

  constructor(private router: Router, private route: ActivatedRoute, private gitLinkService: GitLinkService,
              private themeService: ThemeService) {
    this.isDarkMode = localStorage.getItem('theme') === 'dark';
  }

  /**
   * This method is used make a example of radar chart
   */
  RadarChart(): void {
    const canvasId = `radarChart`;
    const canvas = document.getElementById(canvasId) as HTMLCanvasElement;
    if (!canvas) {
      return;
    }
    const ctx = canvas.getContext('2d');
    if (ctx) {
      const radarChart = new Chart(ctx, {
        type: 'radar',
        data: {
          labels: ['Java', 'Python', 'SQL', 'C++', 'HTML'],
          datasets: [{
            label: 'My First Dataset',
            data: [65, 59, 90, 81, 56],
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1
          }]
        },
        options: {
          elements: {
            line: {
              borderWidth: 3
            }
          },
          scales: {
            x: {
              display: false, // Hide x-axis
              beginAtZero: true
            },
            y: {
              display: false, // Hide y-axis
              beginAtZero: true
            }
          },
          maintainAspectRatio: false, // Disable aspect ratio
          responsive: false, // Disable responsiveness
        }
      });
    }
  }

  /**
   * This method is used make a example of bar chart
   * @constructor
   */
  BarChart(): void {
    const canvasId2 = `barChart`;
    const canvas2 = document.getElementById(canvasId2) as HTMLCanvasElement;
    if (!canvas2) {
      return;
    }
    const ctx2 = canvas2.getContext('2d');
    if (ctx2) {
      const barChart = new Chart(ctx2, {
        type: 'bar',
        data: {
          labels: ['Java', 'Python', 'SQL', 'C++', 'HTML'],
          datasets: [{
            label: 'My First Dataset',
            data: [65, 59, 90, 81, 56],
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1
          }]
        },
        options: {
          elements: {
            line: {
              borderWidth: 3
            }
          },
          scales: {
            x: {
              display: false, // Hide x-axis
              beginAtZero: true
            },
            y: {
              display: false, // Hide y-axis
              beginAtZero: true
            }
          },
          maintainAspectRatio: false, // Disable aspect ratio
          responsive: false, // Disable responsiveness
        }
      });
    }
  }

  /**
   * This method is used to initialize the component with the repository
   */
  ngOnInit(): void {
    const id = parseInt(this.route.snapshot.paramMap.get('id') || '', 10);
    this.loadData(id);
  }

  /**
   * This method is used to toggle the theme
   */
  onToggleTheme(): void {
    this.themeService.toggleTheme();
    this.isDarkMode = !this.isDarkMode;
  }

  /**
   * This method is used to load the data of the repository
   * @param id
   */
  loadData(id: number): void {
    this.gitLinkService.getRepositoryById(id).subscribe(
      (data) => {
        this.repository = JSON.parse(data);
      });
  }

  /**
   * This method is used to take the contributions of the by a tag
   * @param tags
   */
  recupContributions(tags: Tag): void {
    this.actualTag = tags;
    this.numberOfTags = 1;
    this.gitLinkService.getContributionbyTag(tags).subscribe(
      (data: any) => {
        this.contribution = JSON.parse(data);

        const contributions = this.contribution.map(contrib => {
          const username = contrib.contributor.username;
          const contributionData = Object.entries(contrib.languageMap).map(([language, lines]) => ({language, lines}));
          return {username, contributionData};
        });
        this.chartLanguages = Array.from(new Set(contributions.flatMap(contrib => contrib.contributionData.map(c => c.language))));

        this.selectedLanguage = this.chartLanguages[0] || '';
        const usernames = contributions.map(contrib => contrib.username);
        const chartLabels = Array.from(new Set(contributions.flatMap(contrib => contrib.contributionData.map(c => c.language))));
        const chartDataArray = contributions.map(contrib => {
          return chartLabels.map(lang => {
            const contribution = contrib.contributionData.find(c => c.language === lang);
            return contribution ? contribution.lines : 0;
          });
        });
        contributions.forEach((contrib, index) => {
          const chartData = [chartDataArray[index]];
          setTimeout(() => this.createRadarChart(index, chartLabels, chartData, contrib.username), 0);
        });
        setTimeout(() => this.createBarChart(usernames, chartLabels, chartDataArray), 0);
      },
    );
  }


  /**
   * This method is used to take the average contributions of the by a tag
   * @param selectedTag it's actual tag
   * @param numberOfTags it's the number of tag to take
   */
  averageContributions(selectedTag: Tag, numberOfTags: number): void {
    const selectedIndex = this.repository.tags.findIndex((tag: Tag) => tag.name === selectedTag.name);
    const startIndex = Math.max(0, selectedIndex - numberOfTags + 1);
    const tagList: Tag[] = this.repository.tags.slice(startIndex, selectedIndex + 1);
    let totalContributions: AccumulatedContribution = {};
    let processedTags = 0;
    tagList.forEach((tag: Tag) => {
      this.gitLinkService.getContributionbyTag(tag).subscribe(
        (data: string) => {
          const contributions: Contribution[] = JSON.parse(data);
          contributions.forEach((contrib: Contribution) => {
            if (!contrib.contributor || !contrib.contributor.username) {
              return;
            }
            const username = contrib.contributor.username;
            if (!totalContributions[username]) {
              totalContributions[username] = {contributor: contrib.contributor, languageMap: {}};
            }
            if (contrib.languageMap) {
              Object.entries(contrib.languageMap).forEach(([language, lines]) => {
                totalContributions[username].languageMap[language] = (totalContributions[username].languageMap[language] || 0) + lines;
              });
            }
          });
          processedTags++;
          if (processedTags === tagList.length) {
            Object.values(totalContributions).forEach(contrib => {
              Object.keys(contrib.languageMap).forEach(language => {
                contrib.languageMap[language] /= numberOfTags;
              });
            });
            const contributionsArray: Contribution[] = Object.values(totalContributions).map(contrib => ({
              contributor: contrib.contributor,
              languageMap: contrib.languageMap
            }));
            const chartLabels = Array.from(new Set(contributionsArray.flatMap(contrib => Object.keys(contrib.languageMap))));
            const chartDataArray: number[][] = contributionsArray.map(contrib => {
              return chartLabels.map(lang => contrib.languageMap[lang] || 0);
            });
            contributionsArray.forEach((contrib, index) => {
              const chartData = [chartDataArray[index]];
              setTimeout(() => this.createRadarChart(index, chartLabels, chartData, contrib.contributor.username), 0);
            });
          }
        },
      );
    });
  }


  /**
   * This method is used to create a bar chart
   * @param usernames it's the list of username
   * @param chartLabels   it's the list of language
   * @param chartDataArray it's the list of data
   */
  createBarChart(usernames: string[], chartLabels: string[], chartDataArray: number[][]): void {

    const canvasId = 'repoBarChart';
    const canvas = document.getElementById(canvasId) as HTMLCanvasElement;

    if (!canvas) {
      return;
    }

    const existingChart = Chart.getChart(canvas);
    if (existingChart) {
      existingChart.destroy();
    }

    const ctx = canvas.getContext('2d');
    if (ctx) {
      const datasets = chartLabels.map((label, labelIndex) => ({
        label: label,
        data: chartDataArray.map(contribData => contribData[labelIndex]), // Utilisez l'index de l'étiquette
        backgroundColor: this.getRandomColor(),
        borderColor: this.getRandomColor(),
        borderWidth: 1,
      }));

      const barChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: usernames,
          datasets: datasets,
        },
        options: {
          scales: {
            x: {
              stacked: true,
            },
            y: {
              type: 'logarithmic',
              stacked: true,
            },
          },
        },
      });
    }
  }

  /**
   * This method is used to get a random color for a chart
   */
  getRandomColor(): string {
    return `rgba(${Math.floor(Math.random() * 256)}, ${Math.floor(Math.random() * 256)}, ${Math.floor(Math.random() * 256)}, 0.7)`;
  }

  /**
   * This method is used to create a radar chart
   * @param index index of the chart
   * @param chartLabels  it's the list of language
   * @param chartData it's the list of data
   * @param username it's the username
   */
  createRadarChart(index: number, chartLabels: string[], chartData: number[][], username: string): void {
    const canvasId = `radarChart${index}`;
    const canvas = document.getElementById(canvasId) as HTMLCanvasElement;
    if (!canvas) {
      return;
    }
    const existingChart = Chart.getChart(canvas);
    if (existingChart) {
      existingChart.destroy();
    }
    const ctx = canvas.getContext('2d');
    if (ctx) {
      const datasets = chartData.map((data, dataIndex) => ({
        label: username,
        data: data,
        backgroundColor: this.getRandomColor(),
        borderColor: this.getRandomColor(),
        borderWidth: 1,
      }));
      const radarChart = new Chart(ctx, {
        type: 'radar',
        data: {
          labels: chartLabels,
          datasets: datasets,
        },
        options: {
          elements: {
            line: {
              borderWidth: 3
            }
          }
        }
      });
    }
  }

  /**
   * This method is used to go to the home page
   */
  goHome(): void {
    this.router.navigate(['/']);
  }

  /**
   * This method is used to toggle the info bubble
   */
  toggleInfoBubble() {
    this.showInfoBubble = !this.showInfoBubble;
    setTimeout(() => this.RadarChart(), 0);
    setTimeout(() => this.BarChart(), 0);
  }

  /**
   * This method is used to increase the number of tag for a average contribution
   */
  increaseTagNumber() {
    const maxIncrease = this.repository.tags.findIndex((tag: Tag) => tag.name === this.actualTag.name);
    if (this.numberOfTags < maxIncrease + 1) {
      this.numberOfTags++;
      this.averageContributions(this.actualTag, this.numberOfTags);
    }
  }

  /**
   * This method is used to decrease the number of tag for a average contribution
   */
  decreaseTagNumber() {
    if (this.numberOfTags > 1) {
      this.numberOfTags--;
      this.averageContributions(this.actualTag, this.numberOfTags);
    }
  }

  /**
   * This method is used to sort the contributions by language
   * @param language it's the language
   * @param ascending it's the order
   */
      sortContributionsByLanguage(language: string, ascending: boolean = true): void {
        this.contribution.sort((a, b) => {
          const linesA = a.languageMap[language] || 0;
          const linesB = b.languageMap[language] || 0;
          return ascending ? linesA - linesB : linesB - linesA;
        });
        setTimeout(() => this.updateRadarCharts(),0);
      }

  /**
   * This method is used to update the radar chart
   */
  updateRadarCharts(): void {
    const mergedChartLabels = Array.from(new Set(this.contribution.flatMap(contrib => Object.keys(contrib.languageMap))));
    this.contribution.forEach((contrib, index) => {
      const chartData = mergedChartLabels.map(lang => {
        const contribution = contrib.languageMap[lang];
        return contribution ? contribution : 0;
      });

      this.createRadarChart(index, mergedChartLabels, [chartData], contrib.contributor.username);
    });
  }
}
