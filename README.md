# Bitcoin Explorer for Android
The Bitcoin Explorer App is a block explorer for Android. It utilizes the [HTTP API](https://github.com/Blockstream/esplora/blob/master/API.md) from the [Esplora Block Explorer](https://github.com/Blockstream/esplora) by Blockstream. Your can find an online version of the explorer on [blockstream.info](https://blockstream.info). 

## Features
- list latest Bitcoin blocks
    - pull to refresh
    - performant "endless" scrolling
- block details
    - share block detail link leading to [blockstream.info](https://blockstream.info)
- search by block height
- responsive material design

## Building
To compile the app you need to download and install [Android Studio](https://developer.android.com/studio). Clone this repository, open this project in Android Studio and [run the app](https://developer.android.com/studio/run).

A JavaDoc can be found in the projects docs folder.

## License
Bitcoin Explorer for Android is released under the terms of the MIT license. See [LICENSE](https://github.com/CandleHater/bitcoin-explorer-android/blob/master/LICENSE) for more information or see [https://opensource.org/licenses/MIT](https://opensource.org/licenses/MIT).

## TODOs
- tx list in block detail
- caching
- auto reload refresh
