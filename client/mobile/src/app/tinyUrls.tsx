
import { ActivityIndicator, Platform, ScrollView, StyleSheet } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Collapsible } from '@/components/ui/collapsible';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useCallback, useState } from 'react';
import { Url } from '@/types/url.type';
import { urlService } from '@/service/url.service';
import axios from 'axios';
import { UrlList } from '@/components/ui/url-list';
import { useFocusEffect } from 'expo-router';
import { mapDeleteUrlError } from '@/utils/api.error';


export default function TinyUrlsScreen() {

  const [urls, setUrls] = useState<Url[]>([]);
  const [error, setError] = useState<string | null>("");
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const safeAreaInsets = useSafeAreaInsets();
  const insets = {
    ...safeAreaInsets,
    bottom: safeAreaInsets.bottom + BottomTabInset + Spacing.three,
  };
  const theme = useTheme();

  const contentPlatformStyle = Platform.select({
    android: {
      paddingTop: insets.top,
      paddingLeft: insets.left,
      paddingRight: insets.right,
      paddingBottom: insets.bottom,
    },
    web: {
      paddingTop: Spacing.six,
      paddingBottom: Spacing.four,
    },
  });

  const handleDelete = async (url: Url) => {
    try {
      setDeleteError(null);
      await urlService.deleteUrl(url.code);
      setUrls((prev) => prev.filter((u) => u.code !== url.code));
    } catch (e) {
      const message = mapDeleteUrlError(e);
      setDeleteError(message);
      // The link is already gone on the server, so drop it from the list too.
      if (axios.isAxiosError(e) && e.response?.status === 404) {
        setUrls((prev) => prev.filter((u) => u.code !== url.code));
      }
    }
  };



  const load = useCallback(async () => {
    try {
      setError(null);
      setUrls(await urlService.getAllUrls());

    } catch (e) {
      if (axios.isAxiosError(e)) {
        setError(
          e.response
            ? `Server error (${e.response.status})`
            : "Can't reach the server. Check your Wi-Fi and API address."
        );
      } else {
        setError("Something went wrong");
      }
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      load().finally(() => setLoading(false));
    }, [load])
  );

  return (
    <ScrollView
      style={[styles.scrollView, { backgroundColor: theme.background }]}
      contentInset={insets}
      contentContainerStyle={[styles.contentContainer, contentPlatformStyle]}>
      <ThemedView style={styles.container}>
        <ThemedView style={styles.titleContainer}>
          <ThemedText type="subtitle">Urls</ThemedText>
          <ThemedText style={styles.centerText} themeColor="textSecondary">
            Below are the Urls recorded and the Codes generated for them. You can use the codes to access the original Urls.
          </ThemedText>
        </ThemedView>

        <ThemedView style={styles.sectionsWrapper}>
          <Collapsible title="Urls">
            {loading ? (
              <ActivityIndicator size="large" />
            ) : error ? (
              <ThemedText style={styles.centerText}>{error}</ThemedText>
            ) : (
              <UrlList urls={urls} onDelete={handleDelete} />
            )}
          </Collapsible>
        </ThemedView>
      </ThemedView>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scrollView: {
    flex: 1,
  },
  contentContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
  },
  container: {
    maxWidth: MaxContentWidth,
    flexGrow: 1,
  },
  titleContainer: {
    gap: Spacing.three,
    alignItems: 'center',
    paddingHorizontal: Spacing.four,
    paddingVertical: Spacing.six,
  },
  centerText: {
    textAlign: 'center',
  },
  pressed: {
    opacity: 0.7,
  },
  linkButton: {
    flexDirection: 'row',
    paddingHorizontal: Spacing.four,
    paddingVertical: Spacing.two,
    borderRadius: Spacing.five,
    justifyContent: 'center',
    gap: Spacing.one,
    alignItems: 'center',
  },
  sectionsWrapper: {
    gap: Spacing.five,
    paddingHorizontal: Spacing.four,
    paddingTop: Spacing.three,
  },
  collapsibleContent: {
    alignItems: 'center',
  },
  imageTutorial: {
    width: '100%',
    aspectRatio: 296 / 171,
    borderRadius: Spacing.three,
    marginTop: Spacing.two,
  },
  imageReact: {
    width: 100,
    height: 100,
    alignSelf: 'center',
  },
  deleteError: {
    color: '#b91c1c',
    textAlign: 'center',
    paddingBottom: Spacing.two,
  },
});
