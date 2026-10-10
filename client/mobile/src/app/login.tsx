import { useState } from 'react';
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  TextInput,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { userService } from '@/service/user.service';
import { mapAuthError } from '@/utils/auth.error';

export default function LoginScreen() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const router = useRouter();
  const theme = useTheme();
  const insets = useSafeAreaInsets();

  const handleLogin = async () => {
    if (loading) return;

    setError(null);
    setFieldErrors({});

    if (!username.trim() || !password) {
      setError('Enter your username and password.');
      return;
    }

    setLoading(true);
    try {
      await userService.login(username.trim(), password);
      // TODO (next step): the response contains the JWT; save it before navigating
      router.replace('/');
    } catch (e) {
      const mapped = mapAuthError(e);
      setError(mapped.message);
      setFieldErrors(mapped.fieldErrors);
    } finally {
      setLoading(false);
    }
  };

  const inputStyle = [styles.input, { backgroundColor: theme.backgroundElement, color: theme.text }];

  return (
    <KeyboardAvoidingView
      style={[styles.flex, { backgroundColor: theme.background }]}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <ScrollView
        keyboardShouldPersistTaps="handled"
        contentContainerStyle={[
          styles.contentContainer,
          { paddingTop: insets.top + Spacing.six, paddingBottom: insets.bottom + Spacing.six },
        ]}>
        <ThemedView style={styles.container}>
          <ThemedView style={styles.titleContainer}>
            <ThemedText type="subtitle">Log in</ThemedText>
            <ThemedText style={styles.centerText} themeColor="textSecondary">
              Welcome back. Log in to see and manage your Urls.
            </ThemedText>
          </ThemedView>

          <ThemedView style={styles.form}>
            <ThemedView style={styles.field}>
              <TextInput
                style={inputStyle}
                placeholder="Username"
                placeholderTextColor={theme.textSecondary}
                value={username}
                onChangeText={setUsername}
                autoCapitalize="none"
                autoCorrect={false}
                autoComplete="username"
                textContentType="username"
                returnKeyType="next"
                editable={!loading}
              />
              {fieldErrors.username ? <ThemedText style={styles.errorText}>{fieldErrors.username}</ThemedText> : null}
            </ThemedView>

            <ThemedView style={styles.field}>
              <TextInput
                style={inputStyle}
                placeholder="Password"
                placeholderTextColor={theme.textSecondary}
                value={password}
                onChangeText={setPassword}
                secureTextEntry
                autoCapitalize="none"
                autoComplete="password"
                textContentType="password"
                returnKeyType="go"
                onSubmitEditing={handleLogin}
                editable={!loading}
              />
              {fieldErrors.password ? <ThemedText style={styles.errorText}>{fieldErrors.password}</ThemedText> : null}
            </ThemedView>

            {error ? <ThemedText style={[styles.errorText, styles.centerText]}>{error}</ThemedText> : null}

            <Pressable
              onPress={handleLogin}
              disabled={loading}
              style={({ pressed }) => [
                styles.button,
                { backgroundColor: theme.text },
                (pressed || loading) && styles.pressed,
              ]}>
              {loading ? (
                <ActivityIndicator color={theme.background} />
              ) : (
                <ThemedText style={{ color: theme.background }}>Log in</ThemedText>
              )}
            </Pressable>

            <Pressable onPress={() => router.replace('/signup')} disabled={loading} style={styles.switchLink}>
              <ThemedText themeColor="textSecondary" style={styles.centerText}>
                Don't have an account? Sign up
              </ThemedText>
            </Pressable>
          </ThemedView>
        </ThemedView>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  flex: {
    flex: 1,
  },
  contentContainer: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  container: {
    width: '100%',
    maxWidth: MaxContentWidth,
  },
  titleContainer: {
    gap: Spacing.three,
    alignItems: 'center',
    paddingHorizontal: Spacing.four,
    paddingBottom: Spacing.five,
  },
  centerText: {
    textAlign: 'center',
  },
  form: {
    gap: Spacing.three,
    paddingHorizontal: Spacing.four,
  },
  field: {
    gap: Spacing.one,
  },
  input: {
    borderRadius: Spacing.three,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.three,
    fontSize: 16,
  },
  errorText: {
    color: '#b91c1c',
  },
  button: {
    borderRadius: Spacing.five,
    paddingVertical: Spacing.three,
    alignItems: 'center',
    justifyContent: 'center',
  },
  pressed: {
    opacity: 0.7,
  },
  switchLink: {
    paddingVertical: Spacing.two,
  },
});