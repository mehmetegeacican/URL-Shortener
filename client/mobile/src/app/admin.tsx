import { use, useCallback, useState } from 'react';
import { ActivityIndicator, Platform, Pressable, ScrollView, StyleSheet } from 'react-native';
import { Redirect, useFocusEffect } from 'expo-router';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Collapsible } from '@/components/ui/collapsible';
import { AdminUrlTable } from '@/components/ui/admin-url-table';
import { MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { adminService } from '@/service/admin.service';
import { AdminUrl, PageResponse } from '@/types/admin.type';
import { mapAdminError } from '@/utils/admin.error';
import { useUserContext } from '@/contexts/userContext';

const PAGE_SIZE = 10;

// The admin page only exists on web
export default function AdminRoute() {
    if (Platform.OS !== 'web') {
        return <Redirect href="/" />;
    }
    return <AdminScreen />;
}

function AdminScreen() {
    const [data, setData] = useState<PageResponse<AdminUrl> | null>(null);
    const [page, setPage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const {state} = useUserContext();

    const theme = useTheme();

    const load = useCallback(async (pageToLoad: number) => {
        const token = state.token;
        if (!token) {
            setError('Log in as an admin to see this page.');
            setLoading(false);
            return;
        }

        try {
            setLoading(true);
            setError(null);
            const result = await adminService.getUrls(token, { page: pageToLoad, size: PAGE_SIZE });
            setData(result);
        } catch (e) {
            setError(mapAdminError(e));
        } finally {
            setLoading(false);
        }
    }, []);

    // Runs when the screen gains focus and again whenever the page number changes
    useFocusEffect(
        useCallback(() => {
            load(page);
        }, [load, page])
    );

    const lastPage = data ? Math.max(data.totalPages - 1, 0) : 0;

    return (
        <ScrollView
            style={[styles.scrollView, { backgroundColor: theme.background }]}
            contentContainerStyle={styles.contentContainer}>
            <ThemedView style={styles.container}>
                <ThemedView style={styles.titleContainer}>
                    <ThemedText type="subtitle">Admin</ThemedText>
                    <ThemedText style={styles.centerText} themeColor="textSecondary">
                        Every short URL in the system, including deleted ones.
                    </ThemedText>
                </ThemedView>

                <ThemedView style={styles.sectionsWrapper}>
                    <Collapsible title="All URLs">
                        {loading ? (
                            <ActivityIndicator size="large" />
                        ) : error ? (
                            <ThemedText style={styles.errorText}>{error}</ThemedText>
                        ) : data ? (
                            <>
                                <AdminUrlTable urls={data.content} />
                                <ThemedView style={styles.pagination}>
                                    <PageButton label="Previous" disabled={page === 0} onPress={() => setPage((p) => p - 1)} />
                                    <ThemedText type="small" themeColor="textSecondary">
                                        Page {data.page + 1} of {Math.max(data.totalPages, 1)} · {data.totalElements} URLs
                                    </ThemedText>
                                    <PageButton label="Next" disabled={page >= lastPage} onPress={() => setPage((p) => p + 1)} />
                                </ThemedView>
                            </>
                        ) : null}
                    </Collapsible>
                </ThemedView>
            </ThemedView>
        </ScrollView>
    );
}

function PageButton({ label, disabled, onPress }: { label: string; disabled: boolean; onPress: () => void }) {
    const theme = useTheme();
    return (
        <Pressable
            onPress={onPress}
            disabled={disabled}
            style={[styles.pageButton, { backgroundColor: theme.backgroundElement }, disabled && styles.disabled]}>
            <ThemedText type="small">{label}</ThemedText>
        </Pressable>
    );
}

const styles = StyleSheet.create({
    scrollView: {
        flex: 1,
    },
    contentContainer: {
        flexDirection: 'row',
        justifyContent: 'center',
        paddingTop: Spacing.six,
        paddingBottom: Spacing.four,
    },
    container: {
        maxWidth: MaxContentWidth,
        width: '100%',
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
    sectionsWrapper: {
        gap: Spacing.five,
        paddingHorizontal: Spacing.four,
        paddingTop: Spacing.three,
    },
    errorText: {
        color: '#b91c1c',
        textAlign: 'center',
    },
    pagination: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
        paddingTop: Spacing.three,
    },
    pageButton: {
        paddingHorizontal: Spacing.three,
        paddingVertical: Spacing.one,
        borderRadius: Spacing.three,
        marginHorizontal: Spacing.two,
        marginVertical: Spacing.one,
    },
    disabled: {
        opacity: 0.4,
    },
});