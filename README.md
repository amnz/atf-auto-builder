atf-auto-builder
================

指定されたgit作業コピー内に存在するファイルを走査し、プロパティに従いATFファイルを生成します。
生成されたATFファイルは自動的にコミット、プッシュされます。

## 使用法

```Windows
aab.bat your.properties
```


```Linux
aab.sh your.properties
```

## プロパティ設定項目

### git.repository

操作するローカル作業コピーへのパスを指定します。


### git.commit.message

コミットメッセージ

