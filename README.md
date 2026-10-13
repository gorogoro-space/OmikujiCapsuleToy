# OmikujiCapsuleToy
[![Paper 1.21.11](https://img.shields.io/badge/Paper-1.21.11-brightgreen.svg)](https://fill-ui.papermc.io/projects/paper/version/1.21.11)
[![GitHub release](https://img.shields.io/github/release/gorogoro-space/OmikujiCapsuleToy.svg)](https://github.com/gorogoro-space/OmikujiCapsuleToy/releases)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/gorogoro-space/OmikujiCapsuleToy/issues)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://github.com/gorogoro-space/OmikujiCapsuleToy/blob/main/LICENSE)

A Paper plugin (Minecraft) for omikuji (fortune slip) machines. Right-click the sign with an omikuji ticket to get a random item from the chest.

看板とチェストでおみくじを作るプラグインです。おみくじ引換券を手に持って看板を右クリックすると、チェストの中から 1 つランダムにもらえます。

# Requirements
- Paper 1.21.11 or later (1.21.11 以降。それより古いサーバーでは読み込まれません)
- Java 21 or later (21 以降)

# Installation method
Please place the .jar file in the Paper plugins folder.

.jar ファイルを Paper の plugins フォルダに置き、サーバーを再起動してください。
`plugins/OmikujiCapsuleToy/config.yml` を編集したら、`/omikujicapsuletoy reload`(OP 権限が必要)で読み直せます。

# Usage
| コマンド | 説明 | 使える人 |
|---|---|---|
| `/omikujicapsuletoy list` | 登録したおみくじの一覧 | `omikujicapsuletoy.list` |
| `/omikujicapsuletoy modify <名前>` | 中身のチェストを設定する(このあとチェストを右クリック) | `omikujicapsuletoy.modify` |
| `/omikujicapsuletoy delete <名前>` | おみくじを削除する | `omikujicapsuletoy.delete` |
| `/omikujicapsuletoy ticket <プレイヤー名\|@a\|@p\|@s\|@r>` | おみくじ引換券を渡す | OP・コンソール・コマンドブロック |
| `/omikujicapsuletoy reload` | `config.yml` を読み直す | OP |

看板の 1 行目に `[omikujicapsuletoy]` と書いて登録するには `omikujicapsuletoy.create` が必要です。
権限は `plugin.yml` に登録していないため、権限プラグインで与えない限り OP だけが使えます。

1. 看板の 1 行目を `[omikujicapsuletoy]`、2 行目を登録名(英数字と `_` だけ)、3 行目を表示名、4 行目を説明にして設置します。
2. `/omikujicapsuletoy modify <登録名>` のあと、おみくじを入れたチェストを右クリックします。
3. おみくじ引換券を手に持って看板を右クリックすると、券が 1 枚減り、チェストの枠を 1 つランダムにもらえます。空の枠はハズレです。チェストの中身は減りません。

# Config
- `plugins/OmikujiCapsuleToy/config.yml` — 引換券の名前・説明、看板の文字、メッセージなど
- `plugins/OmikujiCapsuleToy/sqlite.db` — 登録した看板とチェストを保存するデータベース

# Migration from ver1.x to ver2.0
2.0 で綴りの誤り(`capslue` → `capsule`)を直しました。プラグイン名・コマンド・権限が変わり、古い名前は使えません。

| | 1.x | 2.0 |
|---|---|---|
| データフォルダ | `plugins/OmikujiCapslueToy/` | `plugins/OmikujiCapsuleToy/` |
| コマンド | `/omikujicapsluetoy` | `/omikujicapsuletoy` |
| 権限 | `omikujicapsluetoy.list` など | `omikujicapsuletoy.list` など |
| 看板の 1 行目 | `[omikujicapsluetoy]` | `[omikujicapsuletoy]` |

1. サーバーを止め、古い jar(`OmikujiCapslueToy-1.0.jar`)を消して、新しい jar を置きます。
2. データフォルダの名前を変えます。
   ~~~
   # cd plugins/
   # cp -rp OmikujiCapslueToy OmikujiCapslueToy.backup
   # mv OmikujiCapslueToy OmikujiCapsuleToy
   ~~~
3. `config.yml` の `not-found-chest2` に古いコマンド名が書いてあれば、`/omikujicapsuletoy modify <capsuletoy_name>` に直します。
4. 権限プラグインで古い権限を与えていた場合は、新しい権限を与え直します。
5. サーバーを起動します。データベースの表と列の名前は、初回の起動時に自動で直ります(ログに `Renamed the database table capsluetoy to capsuletoy.` と出ます)。登録済みの看板とチェストはそのまま使えます。配った引換券もそのまま使えます。

# Disclaimer
Do not assume any responsibility by use. Please use it at your own risk.

## ビルド

JDK 21 が必要です。Gradle はラッパー(`gradlew`)が自動でダウンロードするので、別途インストールする必要はありません。

```
gradlew.bat clean build
```

(macOS / Linux では `./gradlew clean build`)

## IntelliJ IDEA でのビルド手順

本プロジェクトはビルドツールに Gradle を使用しています。
IntelliJ IDEA 上で正しくプラグイン（JARファイル）を生成するには、以下の手順を実行してください。
「ビルド → アーティファクトのビルド」はクラスファイルが入らないことがあるので使わないでください。

### 🛠️ ビルド手順

1. IntelliJ IDEA の画面右端にある **「Gradle」タブ** をクリックして開きます。
2. プロジェクト名（OmikujiCapsuleToy）を展開し、 **`Tasks`** ツリーを開きます。
3. リスト内にある **`clean`** をダブルクリックして実行します（古いビルドキャッシュを削除します）。
4. 続けてリスト内にある **`build`** をダブルクリックして実行します。

### 📦 生成されたファイルの場所
ビルドが成功すると、プロジェクトのルート直下に `build/libs` フォルダが作成（または更新）され、その中に中身の詰まった正しい JAR ファイルが生成されます。

* **生成先:** `build/libs/OmikujiCapsuleToy-<version>.jar`(例: `OmikujiCapsuleToy-2.0.jar`。バージョンは `gradle.properties` の `version`)

この JAR ファイルを Minecraft サーバーの `plugins` フォルダに配置してください。

## 開発(IntelliJ IDEA で Claude Code を使う)

このリポジトリには、AI コーディングツール [Claude Code](https://docs.claude.com/ja/docs/claude-code/overview) 向けの作業ルールを書いた `CLAUDE.md` があります。IntelliJ IDEA で使う手順は次のとおりです。

1. Claude の有料プラン(Pro / Max)か、Anthropic API のアカウントを用意します。
2. Windows では、先に [Git for Windows](https://git-scm.com/downloads/win) をインストールします。
3. Claude Code 本体をインストールします。Windows では PowerShell で次を実行します。
   ```
   irm https://claude.ai/install.ps1 | iex
   ```
4. IntelliJ IDEA の「設定 → プラグイン → Marketplace」で「Claude Code」を検索してインストールし、IDE を再起動します。検索結果には Anthropic 以外が作った似た名前のプラグインも表示されます。プラグイン名の下に書かれた提供元が「Anthropic」になっているものを選んでください。
5. プロジェクトを開いて、ターミナルで `claude` を実行します(`Ctrl+Esc` でも起動できます)。初回はブラウザでログインします。

起動すると `CLAUDE.md` が自動で読み込まれ、このプロジェクトの設計方針に沿って作業します。

## ライセンス

[LICENSE](LICENSE) を参照してください。

## kubotan へのメモ

リポジトリを新規作成したら、**必ず Watch を設定すること**(忘れない!)。
GitHub の自動 Watch 機能は 2025 年 5 月に廃止されたため、設定しないと他の人が立てた issue や PR の通知が届かない。

1. リポジトリのページ右上の **「Watch」** を押す
2. **「Custom」** を選び、**Issues** と **Pull requests** にチェックを入れる
