# nft-tiff

An nf-test plugin to test nft-tiff files

## Start using the plugin

To start using the plugin please add it to your `nf-test.config` file:

```groovy title="nf-test.config"
config {
    plugins {
        load "nft-tiff@0.2.0"
    }
}
```

## Use a development version

To use the development version, please do the following steps:

- Clone the [nft-tiff repository](https://github.com/nf-core/nft-tiff)

=== "HTTPS"

    ```bash
    git clone https://github.com/nf-core/nft-tiff.git
    ```

=== "SSH"

    ```bash
    git clone git@github.com:nf-core/nft-tiff.git
    ```

- Run the build script

```bash
./build.sh
```

- Add the jar location (visible at the end of the build script output) to the `nf-test.config` file

```groovy title="nf-test.config"
config {
    plugins {
        loadFromFile "full/path/to/the/plugin/jar"
    }
}
```

- Or add the plugin jar to an nf-test command:

```bash title="Terminal"
nf-test test --plugins full/path/to/the/plugin/jar
```

!!! note

    The plugin jar will always be located in the `target/` directory in the root of the plugin repository


