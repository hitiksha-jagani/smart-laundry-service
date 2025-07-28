import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import * as Localization from 'expo-localization';

import en from './locales/en/translation';
import hi from './locales/hi/translation';

const locale = Localization.locale || 'en';
const language = locale.split('-')[0];  

i18n.use(initReactI18next).init({
  lng: language,
  fallbackLng: 'en',
  resources: {
    en: { translation: en },
    hi: { translation: hi },
  },
  interpolation: {
    escapeValue: false,
  },
});

export default i18n;
