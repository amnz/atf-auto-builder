atf-auto-builder
================

指定されたgit作業コピー内に存在するpngファイルを走査し、
そのファイルと同じ階層に同名で拡張子 .atf であるファイルが存在するしない場合、
及び存在するがタイムスタンプがpngファイルより古い場合に png2atf を実行しATFファイルを生成します。

生成されたATFファイルは自動的にコミット、プッシュされます。


## 使用法

```DOS:
aab.bat your.properties
```


```Bash:
aab.sh your.properties
```

## プロパティ設定項目

### git.repository

走査するローカルの作業コピーディレクトリ。

### git.commit.message

自動コミット時のコミットメッセージ。

----

### atf.sdkpath

ATFSDKをインストールしたディレクトリ。

### atf.search

作業コピー内のすべてのファイルを走査する場合は、"all"を、
最新のコミット内の更新ファイルのみを走査する場合は "commit" を指定。

### atf.fileTypes

ATFを実行するファイルの拡張子を、”,”区切りで指定。
> 例）atf.fileTypes = png

### atf.(fileType).command

ATFコマンド。
> 例） atf.png.command = png2atf

### atf.(fileType).params

ATFコマンドへ渡すパラメータ
変換する画像ファイルは${input}で、
出力されるATFファイルは${output}で各々指定してください。
> 例） atf.png.params = -c -i ${input} -o ${output}
