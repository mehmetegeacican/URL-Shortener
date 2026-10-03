import { useState } from 'react';
import { Pressable, StyleSheet } from 'react-native';
import * as Clipboard from 'expo-clipboard';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Spacing } from '@/constants/theme';
import { Url } from '@/types/url.type';

type Props = {
  urls: Url[];
};

type CopyCellProps = {
  value: string;
  label?: string;
  copied: boolean;
  onCopy: (value: string) => void;
  bold?: boolean;
};

function CopyCell({ value, label, copied, onCopy, bold }: CopyCellProps) {
  return (
    <ThemedView style={styles.cellContent}>
      <ThemedText
        type={bold ? 'smallBold' : 'small'}
        numberOfLines={1}
        style={styles.cellText}>
        {label ?? value}
      </ThemedText>
      <Pressable
        onPress={() => onCopy(value)}
        hitSlop={8}
        style={({ pressed }) => pressed && styles.pressed}>
        <ThemedText type="small" themeColor="textSecondary">
          {copied ? 'Copied ✓' : 'Copy'}
        </ThemedText>
      </Pressable>
    </ThemedView>
  );
}

export function UrlList({ urls }: Props) {
  const [copiedKey, setCopiedKey] = useState<string | null>(null);

  const handleCopy = async (key: string, value: string) => {
    await Clipboard.setStringAsync(value);
    setCopiedKey(key);
    setTimeout(() => {
      setCopiedKey((current) => (current === key ? null : current));
    }, 1500);
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