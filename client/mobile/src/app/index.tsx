import * as Device from 'expo-device';
import { KeyboardAvoidingView, Platform, Pressable, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useState } from 'react';
import { Url } from '@/types/url.type';
import * as Clipboard from 'expo-clipboard';
import { UrlForm } from '@/components/ui/urlForm';

function getDevMenuHint() {
  if (Platform.OS === 'web') {
    return <ThemedText type="small">use browser devtools</ThemedText>;
  }
  if (Device.isDevice) {
    return (
      <ThemedText type="small">
        shake device or press <ThemedText type="code">m</ThemedText> in terminal
      </ThemedText>
    );
  }
  const shortcut = Platform.OS === 'android' ? 'cmd+m (or ctrl+m)' : 'cmd+d';
  return (
    <ThemedText type="small">
      press <ThemedText type="code">{shortcut}</ThemedText>
    </ThemedText>
  );
}

export default function HomeScreen() {

  const [created, setCreated] = useState<Url | null>(null);
  const [copied, setCopied] = useState(false);

  const shortLink = created ? `${process.env.EXPO_PUBLIC_API_URL}/${created.code}` : '';

  const handleCreated = (url: Url) => {
    setCreated(url);
    setCopied(false);
  };

  const handleCopy = async () => {
    await Clipboard.setStringAsync(shortLink);
    setCopied(true);
    setTimeout(() => setCopied(false), 1500);
  };


  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <KeyboardAvoidingView
          style={styles.heroSection}
          behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
          <ThemedView style={styles.heroSection}>
            <ThemedText type="title" style={styles.title}>
              Enter your URL to shorten it
            </ThemedText>
          </ThemedView>
          <ThemedView style={styles.formWrapper}>
            <UrlForm onCreated={handleCreated} />
          </ThemedView>
          {created && (
            <ThemedView type="backgroundElement" style={styles.result}>
              <ThemedText type="small" themeColor="textSecondary">
                Your code
              </ThemedText>
              <ThemedText type="subtitle">{created.code}</ThemedText>
              <ThemedText type="small" themeColor="textSecondary" numberOfLines={1}>
                {shortLink}
              </ThemedText>
              <Pressable
                onPress={handleCopy}
                style={({ pressed }) => pressed && styles.pressed}>
                <ThemedText type="smallBold">
                  {copied ? 'Copied ✓' : 'Copy link'}
                </ThemedText>
              </Pressable>
            </ThemedView>
          )}
        </KeyboardAvoidingView>
      </SafeAreaView>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    flexDirection: 'row',
  },
  safeArea: {
    flex: 1,
    paddingHorizontal: Spacing.four,
    alignItems: 'center',
    gap: Spacing.three,
    paddingBottom: BottomTabInset + Spacing.three,
    maxWidth: MaxContentWidth,
  },
  heroSection: {
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
    paddingHorizontal: Spacing.four,
    gap: Spacing.four,
  },
  title: {
    textAlign: 'center',
  },
  code: {
    textTransform: 'uppercase',
  },
  stepContainer: {
    gap: Spacing.three,
    alignSelf: 'stretch',
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.four,
    borderRadius: Spacing.four,
  },
  formWrapper: {
    alignSelf: 'stretch',
  },
  pressed: {
    opacity: 0.6,
  },
  result: {
    alignSelf: 'stretch',
    padding: Spacing.three,
    borderRadius: Spacing.three,
    gap: Spacing.one,
  },
});
