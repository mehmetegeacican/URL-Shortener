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

export default function SignupScreen() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const router = useRouter();
  const theme = useTheme();
  const insets = useSafeAreaInsets();

  const handleSignup = async () => {
    if (loading) return;

    setError(null);
    setFieldErrors({});

    if (!username.trim() || !password) {
      setError('Enter a username and a password.');
      return;
    }
    if (password !== confirmPassword) {
      setFieldErrors({ confirmPassword: 'Passwords do not match.' });
      return;
    }

    setLoading(true);
    try {
      await userService.signUp(username.trim(), password);
      // TODO (next step): sign-up also logs the user in, so the response contains the JWT; save it before navigating
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
            <ThemedText type="subtitle">Sign up</ThemedText>
            <ThemedText style={styles.centerText} themeColor="textSecondary">
              Create an account to keep track of your Urls.
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
                autoComplete="username-new"
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
                autoComplete="new-password"
                textContentType="newPassword"
                returnKeyType="next"
                editable={!loading}
              />
              {fieldErrors.password ? <ThemedText style={styles.errorText}>{fieldErrors.password}</ThemedText> : null}
            </ThemedView>

            <ThemedView style={styles.field}>
              <TextInput
                style={inputStyle}
                placeholder="Confirm password"
                placeholderTextColor={theme.textSecondary}
                value={confirmPassword}
                onChangeText={setConfirmPassword}
                secureTextEntry
                autoCapitalize="none"
                autoComplete="new-password"
                textContentType="newPassword"
                returnKeyType="go"
                onSubmitEditing={handleSignup}
                editable={!loading}
              />
              {fieldErrors.confirmPassword ? (
                <ThemedText style={styles.errorText}>{fieldErrors.confirmPassword}</ThemedText>
              ) : null}
            </ThemedView>

            {error ? <ThemedText style={[styles.errorText, styles.centerText]}>{error}</ThemedText> : null}

            <Pressable
              onPress={handleSignup}
              disabled={loading}
              style={({ pressed }) => [
                styles.button,
                { backgroundColor: theme.text },
                (pressed || loading) && styles.pressed,
              ]}>
              {loading ? (
                <ActivityIndicator color={theme.background} />
              ) : (
                <ThemedText style={{ color: theme.background }}>Sign up</ThemedText>
              )}
            </Pressable>

            <Pressable onPress={() => router.navigate('/login')} disabled={loading} style={styles.switchLink}>
              <ThemedText themeColor="textSecondary" style={styles.centerText}>
                Already have an account? Log in
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