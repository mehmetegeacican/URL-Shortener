import { Pressable, StyleProp, StyleSheet, TextStyle } from 'react-native';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Spacing } from '@/constants/theme';
import { AdminUrl } from '@/types/admin.type';
import { useEffect } from 'react';

const formatDate = (iso: string | null) => (iso ? new Date(iso).toLocaleDateString() : '—');

function Cell({
  children,
  style,
  header = false,
}: {
  children: React.ReactNode;
  style?: StyleProp<TextStyle>;
  header?: boolean;
}) {
  return (
    <ThemedText type={header ? 'smallBold' : 'small'} numberOfLines={1} style={style}>
      {children}
    </ThemedText>
  );
}

interface AdminUrlTableProps {
  urls: AdminUrl[];
  onRestore: (url: AdminUrl) => void;
  restoringCode: string | null; // the code being restored right now, if any
}

export function AdminUrlTable({ urls, onRestore, restoringCode }: AdminUrlTableProps) {
  if (urls.length === 0) {
    return (
      <ThemedText themeColor="textSecondary" style={styles.empty}>
        No URLs yet.
      </ThemedText>
    );
  }

  useEffect(() => {
    console.log(urls)
  },[urls])

  return (
    <ThemedView>
      <ThemedView type="backgroundElement" style={styles.row}>
        <Cell header style={styles.code}>Code</Cell>
        <Cell header style={styles.url}>URL</Cell>
        <Cell header style={styles.owner}>Owner</Cell>
        <Cell header style={styles.status}>Status</Cell>
        <Cell header style={styles.created}>Created</Cell>
        <Cell header style={styles.clicks}>Clicks</Cell>
        <Cell header style={styles.actionsHeader}>Actions</Cell>
      </ThemedView>

      {urls.map((u) => {
        const restoring = restoringCode === u.code;
        return (
          <ThemedView key={u.code} style={[styles.row, styles.divider]}>
            <Cell style={styles.code}>{u.code}</Cell>
            <Cell style={styles.url}>{u.url}</Cell>
            <Cell style={styles.owner}>{u.ownerUsername ?? 'anonymous'}</Cell>
            <Cell style={[styles.status, u.deleted && styles.deleted]}>{u.deleted ? 'Deleted' : 'Active'}</Cell>
            <Cell style={styles.created}>{formatDate(u.createdAt)}</Cell>
            <Cell style={styles.clicks}>{u.clickCount}</Cell>

            {/* The cell exists on every row so the columns line up; only deleted rows get a button */}
            <ThemedView style={styles.actionsCell}>
              {u.deleted ? (
                <Pressable
                  onPress={() => onRestore(u)}
                  disabled={restoringCode !== null}
                  style={({ pressed }) => (pressed || restoring) && styles.pressed}>
                  <ThemedView type="backgroundSelected" style={styles.restoreButton}>
                    <ThemedText type="small">{restoring ? 'Restoring…' : 'Restore'}</ThemedText>
                  </ThemedView>
                </Pressable>
              ) : null}
            </ThemedView>
          </ThemedView>
        );
      })}
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
    paddingVertical: Spacing.two,
    paddingHorizontal: Spacing.two,
  },
  divider: {
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: 'rgba(128,128,128,0.25)',
  },
  code: { flex: 1 },
  url: { flex: 3 },
  owner: { flex: 1.5 },
  status: { flex: 1 },
  created: { flex: 1.5 },
  clicks: { flex: 0.7, textAlign: 'right' },
  actionsHeader: { flex: 1.2, textAlign: 'right' },
  actionsCell: { flex: 1.2, alignItems: 'flex-end' },
  restoreButton: {
    paddingVertical: Spacing.one,
    paddingHorizontal: Spacing.two,
    borderRadius: Spacing.three,
  },
  pressed: {
    opacity: 0.6,
  },
  deleted: { color: '#b91c1c' },
  empty: {
    textAlign: 'center',
    paddingVertical: Spacing.three,
  },
});