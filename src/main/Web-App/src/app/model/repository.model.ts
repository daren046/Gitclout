export interface Repository {
  id: number;
  tags: Tag[];
  name: string;
  contributors: Contributor[];
  link: string;
}

export interface Tag {
  id: number;
  name: string;
  sha1: string;
}

export interface Contributor {
  username: string;
}


export interface Contribution {
 contributor: Contributor;
  languageMap: { [language: string]: number };
}


export interface LanguageContributorSums {
  [language: string]: {
    [contributor: string]: number;
  };
}


export interface AccumulatedContribution {
  [username: string]: {
    contributor: { username: string };
    languageMap: { [language: string]: number };
  };
}
