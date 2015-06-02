package com.wondersgroup.cloud.deployment.utils;

import java.sql.Connection;
import java.sql.SQLException;

import org.quartz.utils.ConnectionProvider;

import com.alibaba.druid.pool.DruidDataSource;

public class PooledConnectionProvicer implements ConnectionProvider {

	private String driver, URL, user, password, validationQuery;

	// org.quartz.dataSource.qzDS.driver = com.mysql.jdbc.Driver
	// #org.quartz.dataSource.qzDS.URL =
	// jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8
	// #org.quartz.dataSource.qzDS.user = root
	// #org.quartz.dataSource.qzDS.password = root
	// org.quartz.dataSource.qzDS.maxConnections = 30
	//
	// org.quartz.dataSource.qzDS.URL =
	// jdbc:mysql://10.1.64.41:23306/wcp_v2?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false
	// org.quartz.dataSource.qzDS.user = dbwcp2
	// org.quartz.dataSource.qzDS.password = dbwcp2
	//
	// org.quartz.dataSource.qzDS.validationQuery = select 1
	// org.quartz.dataSource.qzDS.idleConnectionValidationSeconds = 50
	// org.quartz.dataSource.qzDS.validateOnCheckout = false
	// org.quartz.dataSource.qzDS.discardIdleConnectionsSeconds = 1000
	// org.quartz.dataSource.qzDS.connectionProvider.class =
	// com.wondersgroup.cloud.deployment.utils.PooledConnectionProvicer

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public DruidDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DruidDataSource dataSource) {
		this.dataSource = dataSource;
	}

	private DruidDataSource dataSource;

	@Override
	public Connection getConnection() throws SQLException {
		Connection conn = dataSource.getConnection();
		conn.setAutoCommit(true);
		return conn;
	}

	@Override
	public void shutdown() throws SQLException {
		dataSource.close();
	}

	@Override
	public void initialize() throws SQLException {
		dataSource = new DruidDataSource();
		dataSource.setDriverClassName(this.driver);
		dataSource.setUsername(this.user);
		dataSource.setPassword(this.password);
		dataSource.setUrl(this.URL);
		
		dataSource.setInitialSize(5);
		dataSource.setMinIdle(1);
		dataSource.setMaxActive(20);
		
		dataSource.setMaxWait(60000);
		dataSource.setTimeBetweenEvictionRunsMillis(60000);
		dataSource.setMinEvictableIdleTimeMillis(300000);
		dataSource.setValidationQuery(this.validationQuery);
		dataSource.setTestWhileIdle(true);
		
		dataSource.setTestOnBorrow(true);
		dataSource.setTestOnReturn(true);
		
		//dataSource.setPoolPreparedStatements(true);
		//dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
		dataSource.init();
	}

}
