import { NativeTabs } from 'expo-router/unstable-native-tabs';
import { useColorScheme } from 'react-native';
import {useUserContext} from '@/contexts/userContext';

import { Colors } from '@/constants/theme';

export default function AppTabs() {
  const scheme = useColorScheme();
  const colors = Colors[scheme === 'unspecified' ? 'light' : scheme];
  const {state,dispatch} =  useUserContext();

  return (
    <NativeTabs
      backgroundColor={colors.background}
      indicatorColor={colors.backgroundElement}
      labelStyle={{ selected: { color: colors.text } }}>
      <NativeTabs.Trigger name="index">
        <NativeTabs.Trigger.Label>Home</NativeTabs.Trigger.Label>
        <NativeTabs.Trigger.Icon
          src={require('@/assets/images/tabIcons/home.png')}
          renderingMode="template"
        />
      </NativeTabs.Trigger>

      <NativeTabs.Trigger name="tinyUrls">
        <NativeTabs.Trigger.Label>TinyUrls</NativeTabs.Trigger.Label>
        <NativeTabs.Trigger.Icon
          src={require('@/assets/images/tabIcons/explore.png')}
          renderingMode="template"
        />
      </NativeTabs.Trigger>

      <NativeTabs.Trigger name="login" hidden={state.isAuthenticated}>
        <NativeTabs.Trigger.Label>Log in</NativeTabs.Trigger.Label>
        <NativeTabs.Trigger.Icon sf="person.crop.circle" md="person" />
      </NativeTabs.Trigger>

      <NativeTabs.Trigger 
        name="logout" 
        hidden={!state.isAuthenticated}
        listeners={{
          tabPress: (e) => {
            dispatch({ type: 'LOGOUT' });
          }
        }}
      >
        <NativeTabs.Trigger.Label>Log out</NativeTabs.Trigger.Label>
        <NativeTabs.Trigger.Icon sf="person.crop.circle" md="person" />
      </NativeTabs.Trigger>
 
      
      <NativeTabs.Trigger name="signup" hidden />
    </NativeTabs>
  );
}
