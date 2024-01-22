import {Component, Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
/**
 * This service is used to get the theme
 * @Author: Tagnan Tremellat
 */
export class ThemeService {

  constructor() {
    this.loadInitialTheme();
  }

  /**
   * This method is used to load the initial theme
   */
  loadInitialTheme(): void {
    if (localStorage.getItem('theme') === 'dark') {
      document.documentElement.classList.add('dark');
    }
  }

  /**
   * This method is used to toggle the theme
   */
  toggleTheme(): void {
    let theme = 'light';
    if (document.documentElement.classList.contains('dark')) {
      document.documentElement.classList.remove('dark');
      theme = 'light';
    } else {
      document.documentElement.classList.add('dark');
      theme = 'dark';
    }
    localStorage.setItem('theme', theme);
  }
}
