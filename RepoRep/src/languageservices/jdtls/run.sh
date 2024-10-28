# ./org.eclipse.jdt.ls.product/target/repository/bin/jdtls \
# 	-configuration ~/.cache/jdtls \
# 	-data ~/jdtlsdata


# export CLIENT_PORT=5006
# export CLIENT_HOST=localhost

java \
    -Declipse.application=org.eclipse.jdt.ls.core.id1 \
    -Dosgi.bundles.defaultStartLevel=4 \
    -Declipse.product=org.eclipse.jdt.ls.core.product \
    -Dlog.level=ALL \
    -Xmx1G \
    --add-modules=ALL-SYSTEM \
    --add-opens java.base/java.util=ALL-UNNAMED \
    --add-opens java.base/java.lang=ALL-UNNAMED \
    -jar ./plugins/org.eclipse.equinox.launcher_*.jar \
    -configuration ./config_linux \
    -data ~/jdtlsdata
