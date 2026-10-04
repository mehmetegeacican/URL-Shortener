import { Url } from "@/types/url.type";
import { useRef, useState } from "react";
import { useTheme } from "@/hooks/use-theme";
import { urlService } from "@/service/url.service";
import axios from "axios";
import { ThemedView } from "../themed-view";
import { Spacing } from "@/constants/theme";
import { ActivityIndicator, Pressable, StyleSheet, TextInput } from 'react-native';
import { ThemedText } from "../themed-text";
import { CODE_MAX_LENGTH, validateCode } from "@/utils/code.validation";

type Props = {
    onCreated: (createdUrl: Url) => void;
};

export function UrlForm({ onCreated }: Props) {
    const theme = useTheme();
    const [url, setUrl] = useState<string>("");
    const [code, setCode] = useState<string>("");
    const codeInputRef = useRef<TextInput>(null);
    const [codeError, setCodeError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async () => {
        const trimmed = url.trim();
        const trimmedCode = code.trim();

        if (!/^https?:\/\/.+/i.test(trimmed)) {
            setError('Enter a full URL starting with http:// or https://');
            return;
        }

        const codeValidationError = validateCode(trimmedCode);

        if (codeValidationError) {
            setCodeError(codeValidationError);
            return;
        }

        setCodeError(null);

        try {
            setError(null);
            setLoading(true);
            const createdUrl = await urlService.createUrl(trimmed, trimmedCode);
            onCreated(createdUrl);
            setUrl("");
            setCode("");
        } catch (e) {
            if (axios.isAxiosError(e)) {
                setError(
                    e.response
                        ? `Server error (${e.response.status})`
                        : "Can't reach the server. Check your Wi-Fi and API address."
                );
            }
            else {
                setError("Failed to create URL");
            }
        } finally {
            setLoading(false);
        }
    }


    return (
        <ThemedView type='backgroundElement' style={styles.card}>
            <ThemedText type='smallBold'>Shorten a URL</ThemedText>
            <TextInput
                style={[styles.input, { color: theme.text, borderColor: theme.textSecondary }]}
                placeholder="https://example.com/very/long/link"
                placeholderTextColor={theme.textSecondary}
                value={url}
                onChangeText={setUrl}
                autoCapitalize="none"
                autoCorrect={false}
                keyboardType="url"
                editable={!loading}
                onSubmitEditing={handleSubmit}
                returnKeyType="go"
            />
            <TextInput
                ref={codeInputRef}
                style={[
                    styles.input,
                    { color: theme.text, borderColor: codeError ? '#b91c1c' : theme.textSecondary },
                ]}
                placeholder="Custom code (optional)"
                placeholderTextColor={theme.textSecondary}
                value={code}
                onChangeText={(text) => {
                    setCode(text);
                    if (codeError) setCodeError(null);
                }}
                maxLength={CODE_MAX_LENGTH}
                autoCapitalize="none"
                autoCorrect={false}
                editable={!loading}
                returnKeyType="go"
                onSubmitEditing={handleSubmit}
            />
            {codeError && (
                <ThemedText type="small" style={styles.error}>
                    {codeError}
                </ThemedText>
            )}
            {error && (
                <ThemedText type="small" style={styles.error}>
                    {error}
                </ThemedText>
            )}
            <Pressable
                onPress={handleSubmit}
                disabled={loading}
                style={({ pressed }) => [
                    styles.button,
                    (pressed || loading) && styles.pressed,
                ]}>
                {loading ? (
                    <ActivityIndicator color="#fff" />
                ) : (
                    <ThemedText type="smallBold" style={styles.buttonText}>
                        Generate code
                    </ThemedText>
                )}
            </Pressable>
        </ThemedView>
    )
}

const styles = StyleSheet.create({
    card: {
        padding: Spacing.three,
        borderRadius: Spacing.three,
        gap: Spacing.three,
    },
    input: {
        borderWidth: 1,
        borderRadius: Spacing.two,
        paddingHorizontal: Spacing.three,
        paddingVertical: Spacing.two,
        fontSize: 16,
    },
    error: {
        color: '#b91c1c',
    },
    button: {
        backgroundColor: '#4f46e5',
        borderRadius: Spacing.two,
        paddingVertical: Spacing.three,
        alignItems: 'center',
    },
    buttonText: {
        color: '#fff',
    },
    pressed: {
        opacity: 0.6,
    },
});