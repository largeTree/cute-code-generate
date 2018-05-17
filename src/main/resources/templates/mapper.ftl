<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.dao.${className}Dao">

	<sql id="allFields">
		<#list fields as field>
		${field.columnName} <#if field.name != field.columnName>as ${field.name}</#if><#if fields_index < fields?size>,<#/if>
		</#list>
	</sql>

	<sql id="insertFields">
		<#list fields as field>
		${field.columnName}<#if fields_index < fields?size>,<#/if>
		</#if>
	</sql>

	<select id="list" resultType="${packageName}.entity.${className}" >
		select
			<include refid="allFields" />
		from ${tableName}
		<where>
			<include refid="comnWhere" />
		</where>
		<if test="orderBy != null" >
			order by ${orderBy}
			<if test="orderByDesc != null">
				desc
			</if>
		</if>
	</select>

	<sql id="comnWhere">
		<#list fields as field>
		<if test="${field.name} != null">
			<#if fields_index > 0> and </#if>${field.columnName} = #{${field.name}}
		</if>
		</#list>
	</sql>

	<select id="getByIds" resultType="${packageName}.entity.${className}" >
		select <include refid="allFields" /> from user where id in
		<foreach collection="list" item="item" open="(" separator="," close=")" >
			#{item}
		</foreach>
	</select>

	<select id="get" resultType="${packageName}.entity.${className}" >
		select <include refid="allFields" /> from ${tableName} where id = #{id}
	</select>

	<delete id="deleteById" parameterType="Long" >
		delete from ${tableName} where id = #{id}
	</delete>

	<insert id="insert" parameterType="${packageName}.entity.${className}">
		insert into ${tableName}(<include refid="insertFields" />)
		values(
			<#list fields as field>
			#{${field.name}}<#if fields_index < fields?size>,</#if>
			</#list>
		)
	</insert>

	<insert id="insertBatch" parameterType="java.utils.List" >
		insert into ${tableName}(<include refid="insertFields" />)
		values
		<foreach collection="list" item="item" separator="," >
			(
				<#list fields as field>
				#{item.${field.name}}<#if fields_index < fields?size>,</#if>
				</#list>
			)
		</foreach>
	</insert>

	<update id="update" parameterType="${packageName}.entity.${className}" >
		update ${tableName} <include refid="setComn" /> where id = #{id}
	</update>

	<sql id="setComn" >
	<#list fields as field>
		<if test="${field.name} != null" >
			${field.columnName} = #{${field.name}}<#if fields_index < fields?size>,</#if>
		</if>
	</#list>
	</sql>
</mapper>