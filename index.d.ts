
declare module 'react-native-onelogin' {
  export function setAdditionalPrivacyTerms(items: any);

  export function requestToken(appKey: string, debug: boolean): Promise<any>

  export function dismissAuth(): void
}
