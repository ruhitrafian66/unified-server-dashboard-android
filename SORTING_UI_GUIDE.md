# Torrent Sorting - UI Design Guide

## Visual Layout

### Active Torrents Screen - Sort Controls

```
┌─────────────────────────────────────────────────────────────┐
│ Torrents                                          [↕] [🔍]   │
└─────────────────────────────────────────────────────────────┘
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ Sort Menu (when [↕] is tapped)                          │ │
│ │ ─────────────────────────────────────────────────────── │ │
│ │ ☐ Name                                            ↓     │ │
│ │ ☐ Size                                                  │ │
│ │ ☐ Seeds                                                 │ │
│ │ ☐ Progress                                              │ │
│ │ ☐ Ratio                                                 │ │
│ │ ☐ Download Speed                                        │ │
│ └─────────────────────────────────────────────────────────┘ │
│                                                              │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ 📈 10 torrent(s)        ↓ 2.5MB/s    ↑ 1.2MB/s         │ │
│ └─────────────────────────────────────────────────────────┘ │
│                                                              │
│ ☑ Ubuntu 22.04 LTS                           ⏸  [Progress]│
│   Downloaded: 2.1GB / 3.4GB  Ratio: 1.23                   │
│   ↓ 850KB/s  ↑ 320KB/s  ETA: 45m              [────────]   │
│                                                              │
│ ☑ Debian 11.6                                 ▶  [Progress]│
│   Downloaded: 3.1GB / 3.1GB  Ratio: 0.95                   │
│   Seeding                                      [──────────] │
│                                                              │
```

### Search Results - Sort Controls

```
┌─────────────────────────────────────────────────────────────┐
│ Search Torrents                               [↕] (appears  │
│                                               when results) │
│ [Search Input Box]                                          │
│                                               [Search] [Stop]│
│                                                              │
│ 247 results                                                  │
│                                                              │
│ ☑ Linux-Kernel-6.4.tar.gz                                  │
│   2.3MB  S:5432 L:234  kernel.org                           │
│                                                      [+ Add] │
│                                                              │
│ ☑ Ubuntu-22.04-Desktop-amd64.iso                           │
│   3.2GB  S:8932 L:156  ubuntu.com                           │
│                                                      [+ Add] │
│                                                              │
│ ☑ Linux-Headers-6.4-generic.tar.gz                         │
│   892MB  S:3421 L:89   kernel.org                           │
│                                                      [+ Add] │
│                                                              │
```

## Sort Direction Indicators

### Current Sort: Name (Descending) ↓
```
☐ Name                                            ↓
```

### Current Sort: Size (Ascending) ↑
```
☐ Size                                            ↑
```

### Current Sort: Seeds (Descending) ↓
```
☐ Seeds                                           ↓
```

## Sort Flow Diagram

```
User Opens Torrents Screen
        ↓
Display torrents in Default Order
(Date Added, Descending - newest first)
        ↓
User Taps [↕] Sort Icon
        ↓
Dropdown Menu Appears
With All Sort Options
        ↓
User Selects Sort Option
        ↓
        ├─ If Different Option:
        │  └─ Sort by new criteria (Descending)
        │
        └─ If Same Option:
           └─ Reverse Sort Direction
        ↓
Torrents Re-Render with New Order
```

## State Management Flow

```
TorrentsViewModel
├─ State: torrentSortOption (default: DATE_ADDED)
├─ State: torrentSortOrder (default: DESCENDING)
│
├─ setTorrentSort(option)
│  └─ Update sort preference
│     Toggle direction if same option
│
├─ getSortedTorrents()
│  └─ Apply current sort to list
│
└─ sortTorrents(list, option, order)
   └─ Perform actual sort operation
```

## Search Results Independent Sorting

```
Search Results Sort State (Independent)
├─ searchResultsSortOption (default: SEEDS)
├─ searchResultsSortOrder (default: DESCENDING)
│
├─ setSearchResultsSort(option)
│  └─ Update search sort preference
│
└─ getSortedSearchResults()
   └─ Apply search-specific sort
```

## Interaction Patterns

### Pattern 1: Sort by Size (Largest First)

```
1. User taps [↕]
   ↓
2. Dropdown menu appears
   ↓
3. User selects "Size"
   ↓
4. Torrents sort: largest → smallest
   ↓
5. Icon shows [↓] next to "Size"
```

### Pattern 2: Toggle Direction

```
1. Currently sorted by "Size" (Descending) with [↓]
   ↓
2. User taps [↕]
   ↓
3. Dropdown menu appears
   ↓
4. User selects "Size" again
   ↓
5. Direction reverses: smallest → largest
   ↓
6. Icon changes to [↑] next to "Size"
```

### Pattern 3: Switch Sort Criteria

```
1. Currently sorted by "Size" (Descending)
   ↓
2. User taps [↕]
   ↓
3. User selects "Seeds"
   ↓
4. Sort changes to Seeds (Descending - most first)
   ↓
5. Icon shows [↓] next to "Seeds", not "Size"
```

## Colors & Styling

- **Sort Icon**: Primary color (blue)
- **Dropdown Menu**: Surface color background
- **Selected Option**: Primary color text + arrow indicator
- **Arrow Indicator**: Primary color (↑↓)
- **Menu Items**: Standard Material3 styling

## Accessibility Considerations

- Sort button has descriptive "Sort" label
- Arrow indicators provide visual feedback
- Dropdown menu is keyboard navigable
- Touch targets minimum 48dp (sort button + menu items)
- Screen reader: "Sort button, currently sorted by Date Added Descending"

## Default Sort Behaviors

### Active Torrents List
```
Primary Sort: DATE_ADDED
Direction: DESCENDING (newest first)
Reason: Recent activity most relevant
```

### Search Results
```
Primary Sort: SEEDS
Direction: DESCENDING (most seeds first)
Reason: Popular torrents = better health
```

## Sort Option Descriptions

| Option | Description | Use Case |
|--------|-------------|----------|
| **Name** | Alphabetical by torrent name | Finding specific torrent |
| **Size** | By total torrent size (bytes) | Large/small file management |
| **Seeds** | By seeder count | Connection/speed quality |
| **Progress** | By completion percentage | Monitoring progress |
| **Ratio** | By upload/download ratio | Seed health assessment |
| **Download Speed** | By current down speed | Active bandwidth usage |

## Animation & Transitions

Current Implementation: None (instant sort)

Potential Future Enhancements:
- Fade transition when list re-sorts
- Subtle expand/collapse of dropdown
- Arrow rotation animation (↓→↑)
- Stagger animation for list items

## Error States

### No Torrents
- Sort button present but dropdown disabled
- Displays "No torrents" message

### Single Torrent
- Sort button present, single item shown
- All sort options available (for consistency)

### Empty Search Results
- Sort button hidden until results appear
- Appears when first result added
