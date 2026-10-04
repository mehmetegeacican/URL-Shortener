import { useState } from 'react';
import { Pressable, StyleSheet, ActivityIndicator } from 'react-native';
import * as Clipboard from 'expo-clipboard';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Spacing } from '@/constants/theme';
import { Url } from '@/types/url.type';
import { confirmAction } from '@/utils/confirm';
import { Ionicons } from '@expo/vector-icons';


type Props = {
  urls: Url[];
  onDelete: (url: Url) => Promise<void>; // NEW
};

type CopyCellProps = {
  value: string;
  label?: string;
  copied: boolean;
  onCopy: (value: string) => void;
  bold?: boolean;
  copyRemoved?: boolean;
};

function CopyCell({ value, label, copied, onCopy, bold, copyRemoved = false }: CopyCellProps) {
  return (
    <ThemedView style={styles.cellContent}>
      <ThemedText
        type={bold ? 'smallBold' : 'small'}
        numberOfLines={1}
        style={styles.cellText}>
        {label ?? value}
      </ThemedText>
      {!copyRemoved && (
        <Pressable
          onPress={() => onCopy(value)}
          hitSlop={8}
          style={({ pressed }) => pressed && styles.pressed}>
          <ThemedText type="small" themeColor="textSecondary">
            {copied ? 'Copied ✓' : 'Copy'}
          </ThemedText>
        </Pressable>
      )}
    </ThemedView>
  );
}

export function UrlList({ urls, onDelete }: Props) {
  const [copiedKey, setCopiedKey] = useState<string | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null); // NEW

  const handleCopy = async (key: string, value: string) => {
    await Clipboard.setStringAsync(value);
    setCopiedKey(key);
    setTimeout(() => {
      setCopiedKey((current) => (current === key ? null : current));
    }, 1500);
  };

  const handleDelete = async (item: Url) => {
    const confirmed = await confirmAction(
      'Delete link?',
      `The code ${item.code} will stop working.`
    );
    if (!confirmed) return;
    setDeletingId(item.id);
    try {
      await onDelete(item);
    } finally {
      setDeletingId(null);
    }
  };

  if (urls.length === 0) {
    return (
      <ThemedText themeColor="textSecondary" style={styles.empty}>
        No links yet
      </ThemedText>
    );
  }

  return (
    <ThemedView type="backgroundElement" style={styles.table}>
      {/* Header row */}
      <ThemedView type="backgroundElement" style={[styles.row, styles.headerRow]}>
        <ThemedText type="smallBold" themeColor="textSecondary" style={styles.urlCol}>
          URL
        </ThemedText>
        <ThemedText type="smallBold" themeColor="textSecondary" style={styles.codeCol}>
          Code
        </ThemedText>
        <ThemedView type="backgroundElement" style={styles.actionCol} />
      </ThemedView>

      {/* Data rows */}
      {urls.map((item, index) => (
        <ThemedView
          key={item.id}
          type="backgroundElement"
          style={[styles.row, index < urls.length - 1 && styles.rowDivider]}>
          <ThemedView type="backgroundElement" style={styles.urlCol}>
            <CopyCell
              value={item.url}
              copied={copiedKey === `url-${item.id}`}
              onCopy={(v) => handleCopy(`url-${item.id}`, v)}
              copyRemoved={true}
            />
          </ThemedView>
          <ThemedView type="backgroundElement" style={styles.codeCol}>
            <CopyCell
              label={item.code}
              value={`${process.env.EXPO_PUBLIC_API_URL}/${item.code}`}
              bold
              copied={copiedKey === `code-${item.id}`}
              onCopy={(v) => handleCopy(`code-${item.id}`, v)}
            />
          </ThemedView>
          <ThemedView type="backgroundElement" style={styles.actionCol}>
            <Pressable
              onPress={() => handleDelete(item)}
              disabled={deletingId === item.id}
              hitSlop={12}
              accessibilityRole="button"
              accessibilityLabel={`Delete link ${item.code}`}
              style={({ pressed }) => pressed && styles.pressed}>
              {deletingId === item.id ? (
                <ActivityIndicator size="small" color="#b91c1c" />
              ) : (
                <Ionicons name="trash-outline" size={20} color="#b91c1c" />
              )}
            </Pressable>
          </ThemedView>
        </ThemedView>
      ))}
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  table: {
    borderRadius: Spacing.three,
    overflow: 'hidden',
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
    gap: Spacing.three,
  },
  headerRow: {
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: 'rgba(128,128,128,0.4)',
  },
  rowDivider: {
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: 'rgba(128,128,128,0.25)',
  },
  urlCol: {
    flex: 2,
  },
  codeCol: {
    flex: 1,
  },
  // NEW
  actionCol: {
    width: 56,
    alignItems: 'flex-end',
  },
  // NEW
  deleteText: {
    color: '#b91c1c',
  },
  cellContent: {
    backgroundColor: 'transparent',
    gap: Spacing.half,
  },
  cellText: {
    flexShrink: 1,
  },
  pressed: {
    opacity: 0.6,
  },
  empty: {
    textAlign: 'center',
    paddingVertical: Spacing.five,
  },
});