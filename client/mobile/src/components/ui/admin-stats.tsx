import { StyleSheet } from 'react-native';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Spacing } from '@/constants/theme';
import { AdminStats } from '@/types/admin.type';

interface Column {
    label: string;
    flex: number;
    alignRight?: boolean;
}

type Row = (string | number)[];

function DataTable({
    title,
    columns,
    rows,
    emptyText = 'No data yet.',
}: {
    title: string;
    columns: Column[];
    rows: Row[];
    emptyText?: string;
}) {
    return (
        <ThemedView style={styles.table}>
            <ThemedText type="smallBold">{title}</ThemedText>

            {rows.length === 0 ? (
                <ThemedText type="small" themeColor="textSecondary">
                    {emptyText}
                </ThemedText>
            ) : (
                <ThemedView>
                    <ThemedView type="backgroundElement" style={styles.row}>
                        {columns.map((c) => (
                            <ThemedText
                                key={c.label}
                                type="smallBold"
                                numberOfLines={1}
                                style={[{ flex: c.flex }, c.alignRight && styles.right]}>
                                {c.label}
                            </ThemedText>
                        ))}
                    </ThemedView>

                    {rows.map((row, i) => (
                        <ThemedView key={i} style={[styles.row, styles.divider]}>
                            {row.map((cell, j) => (
                                <ThemedText
                                    key={j}
                                    type="small"
                                    numberOfLines={1}
                                    style={[{ flex: columns[j].flex }, columns[j].alignRight && styles.right]}>
                                    {typeof cell === 'number' ? cell.toLocaleString() : cell}
                                </ThemedText>
                            ))}
                        </ThemedView>
                    ))}
                </ThemedView>
            )}
        </ThemedView>
    );
}

export function AdminStatsTables({ stats }: { stats: AdminStats }) {
    const overview: Row[] = [
        ['Total URLs', stats.urls.total],
        ['Active URLs', stats.urls.active],
        ['Deleted URLs', stats.urls.deleted],
        ['Anonymous URLs', stats.urls.anonymous],
        ['URLs created today', stats.urls.createdToday],
        ['Total users', stats.users.total],
        ['Admins', stats.users.admins],
        ['Total clicks', stats.clicks.total],
        ['Clicks today', stats.clicks.today],
        ['Unique IPs', stats.clicks.uniqueIps],
    ];

    return (
        <ThemedView style={styles.container}>
            <DataTable
                title="Overview"
                columns={[
                    { label: 'Metric', flex: 3 },
                    { label: 'Value', flex: 1, alignRight: true },
                ]}
                rows={overview}
            />

            <DataTable
                title="Top URLs"
                columns={[
                    { label: 'Code', flex: 1 },
                    { label: 'URL', flex: 3 },
                    { label: 'Clicks', flex: 1, alignRight: true },
                ]}
                rows={stats.topUrls.map((u) => [u.code, u.url, u.clicks])}
                emptyText="No clicks recorded yet."
            />

            <DataTable
                title="Top IPs"
                columns={[
                    { label: 'IP', flex: 3 },
                    { label: 'Clicks', flex: 1, alignRight: true },
                ]}
                rows={stats.topIps.map((p) => [p.ip, p.clicks])}
                emptyText="No clicks recorded yet."
            />

            <DataTable
                title="Clicks per day"
                columns={[
                    { label: 'Date', flex: 3 },
                    { label: 'Clicks', flex: 1, alignRight: true },
                ]}
                rows={stats.clicksPerDay.map((d) => [d.date, d.clicks])}
                emptyText="No clicks recorded yet."
            />
        </ThemedView>
    );
}

const styles = StyleSheet.create({
    container: {
        gap: Spacing.four,
    },
    table: {
        gap: Spacing.two,
    },
    row: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: Spacing.two,
        paddingVertical: Spacing.two,
        paddingHorizontal: Spacing.three,
    },
    divider: {
        borderBottomWidth: StyleSheet.hairlineWidth,
        borderBottomColor: 'rgba(128,128,128,0.25)',
    },
    right: {
        textAlign: 'right',
    },
});