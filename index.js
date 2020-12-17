import {NativeModules} from 'react-native';

const {OneLogin} = NativeModules;

export function setAdditionalPrivacyTerms(items: any) {
  OneLogin.setAdditionalPrivacyTerms(items)
}

export function requestToken(appKey: string, debug: boolean): Promise<any> {
  return OneLogin.requestToken(appKey, debug);
}

export function dismissAuth() {
  OneLogin.dismissAuth();
}
