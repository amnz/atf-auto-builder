atf-auto-builder
================

指定されたgit作業コピー内に存在するファイルを走査し、プロパティに従いATFファイルを生成します。
生成されたATFファイルは自動的にコミット、プッシュされます。

## 使用法

```DOS:Windows
aab.bat your.properties
```


```Bash:Linux
aab.sh your.properties
```

## プロパティ設定項目

### git.repository

走査するローカル作業コピーのディレクトリ


### git.commit.message

コミットメッセージ

### atf.sdkpath

ATFSDKをインストールしたディレクトリ

### atf.search

作業コピー内のすべてのファイルを走査する場合は、"all"を、
最新のコミット内の更新ファイルのみを走査する場合は "commit" を指定

### atf.fileTypes

ATFを実行するファイルの拡張子を、”,”区切りで指定。
> 例） png, gif, jpg

### atf.(fileType).command

ATFコマンド

### atf.(fileType).params

ATFコマンドへ渡すパラメータ
変換する画像ファイルは${input}で、
出力されるATFファイルは${output}で各々指定してください。