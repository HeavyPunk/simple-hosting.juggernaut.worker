import common.configuration.YamlFileConfigurationProvider
import common.configuration.Settings

@main def main: Unit = {
    val configPath = "settings.yml"
    val config = YamlFileConfigurationProvider(configPath)
        .getConfig
}
