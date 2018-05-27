package com.runjf.mybatis.crud;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.java.TypeParameter;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * 生成Model、Mapper继承的父类和接口
 *
 * Created by rjf on 2018/5/12.
 */
public class GeneratorPlugin extends PluginAdapter {

    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        String clientSuperInterface = properties.getProperty("clientSuperInterface", "com.runjf.mybatis.crud.BaseMapper#M#K");
        addClass(clientSuperInterface, introspectedTable, interfaze::addImportedType, interfaze::addSuperInterface);

        FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.QueryExpressionDSL"));
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter"));

        // applyWhereSelective QueryExpressionDSL
        Method method = new Method();
//        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("QueryExpressionDSL.QueryExpressionWhereBuilder") {
//            @Override
//            public String getFullyQualifiedName() {
//                return "QueryExpressionDSL<R>.QueryExpressionWhereBuilder";
//            }
//        };
//        method.setReturnType(returnType);
//        method.getParameters().add(new Parameter(new FullyQualifiedJavaType("QueryExpressionDSL<R>"), "dsl"));
        addDSLWhereSelective(introspectedTable, modelType, method, "QueryExpressionDSL<R>", "QueryExpressionWhereBuilder");
        interfaze.addMethod(method);

        // applyWhereSelective UpdateDSL
        method = new Method();
        addDSLWhereSelective(introspectedTable, modelType, method, "UpdateDSL<R>", "UpdateWhereBuilder");
        interfaze.addMethod(method);

        // applyWhereSelective DeleteDSL
        method = new Method();
        addDSLWhereSelective(introspectedTable, modelType, method, "DeleteDSL<R>", "DeleteWhereBuilder");
        interfaze.addMethod(method);

        // applyWhereSelective AbstractWhereDSL
        method = new Method();
        addDSLWhereSelective(introspectedTable, modelType, method, "D", null);
        method.getTypeParameters().clear();
        method.getTypeParameters().add(new TypeParameter("D extends AbstractWhereDSL<D>"));
        method.setReturnType(new FullyQualifiedJavaType("D"));
        interfaze.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.where.AbstractWhereDSL"));
        interfaze.addMethod(method);

        // selectByExampleWhereSelective
        method = new Method();
        method.setDefault(true);
        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, new HashSet<>());
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("QueryExpressionDSL.QueryExpressionWhereBuilder") {
            @Override
            public String getFullyQualifiedName() {
                return "QueryExpressionDSL<MyBatis3SelectModelAdapter<List<"
                        + modelType.getShortNameWithoutTypeArguments()
                        + ">>>.QueryExpressionWhereBuilder";
            }
        };
        method.setReturnType(returnType);
        method.setName("selectByExampleWhereSelective");
        method.getParameters().add(new Parameter(modelType, "params_"));
        method.addBodyLine("return applyWhereSelective(this.selectByExample(), params_);");
        interfaze.addMethod(method);

        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (!primaryKeyColumns.isEmpty() && primaryKeyColumns.size() == 1) { // 不支持联合主键
            IntrospectedColumn primaryKeyColumn = primaryKeyColumns.get(0);
            HashSet<FullyQualifiedJavaType> imports = new HashSet<>();

            // selectAllByPrimaryKey
            method = new Method();
            method.setDefault(true);
            context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);
            returnType = new FullyQualifiedJavaType("List");
            returnType.addTypeArgument(modelType);
            method.setReturnType(returnType);
            method.setName("selectAllByPrimaryKey");
            FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("List");
            paramType.addTypeArgument(primaryKeyColumn.getFullyQualifiedJavaType());
            method.getParameters().add(new Parameter(paramType, "ids_"));
            method.addBodyLine("return this.selectByExample()");
            method.addBodyLine("        .where("+ primaryKeyColumn.getJavaProperty() +", isIn(ids_))");
            method.addBodyLine("        .build()");
            method.addBodyLine("        .execute();");
            interfaze.addMethod(method);

            // deleteAllByPrimaryKey
            method = new Method();
            method.setDefault(true);
            context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);
            method.setReturnType(new FullyQualifiedJavaType("int"));
            method.setName("deleteAllByPrimaryKey");
            paramType = new FullyQualifiedJavaType("List");
            paramType.addTypeArgument(primaryKeyColumn.getFullyQualifiedJavaType());
            method.getParameters().add(new Parameter(paramType, "ids_"));
            method.addBodyLine("return this.deleteByExample()");
            method.addBodyLine("        .where("+ primaryKeyColumn.getJavaProperty() +", isIn(ids_))");
            method.addBodyLine("        .build()");
            method.addBodyLine("        .execute();");
            interfaze.addMethod(method);

            // existsByPrimaryKey
            method = new Method();
            method.setDefault(true);
            context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);
            method.setReturnType(new FullyQualifiedJavaType("boolean"));
            method.setName("existsByPrimaryKey");
            method.getParameters().add(new Parameter(primaryKeyColumn.getFullyQualifiedJavaType(), "id_"));
            method.addBodyLine("return this.countByExample()");
            method.addBodyLine("        .where("+ primaryKeyColumn.getJavaProperty() +", isEqualTo(id_))");
            method.addBodyLine("        .build()");
            method.addBodyLine("        .execute() == 1;");
            interfaze.addMethod(method);
        }

        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        String modelSuperClass = properties.getProperty("modelSuperClass");
        addClass(modelSuperClass, introspectedTable, topLevelClass::addImportedType, topLevelClass::setSuperClass);

        String modelSuperInterface = properties.getProperty("modelSuperInterface", "com.runjf.mybatis.crud.Identity#K");
        addClass(modelSuperInterface, introspectedTable, topLevelClass::addImportedType, topLevelClass::addSuperInterface);

        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String getId = "getId";
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns.size() == 1 // 不支持联合主键
                && !getId.equals(method.getName())
                && primaryKeyColumns.contains(introspectedColumn)) {
            Method idMethod = new Method();
            idMethod.setVisibility(JavaVisibility.PUBLIC);
            idMethod.setReturnType(method.getReturnType());
            idMethod.setName(getId);
            idMethod.getParameters().addAll(method.getParameters());
            idMethod.addBodyLine("return this." + method.getName() + "();");
            topLevelClass.addMethod(idMethod);
        }
        return true;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String setId = "setId";
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns.size() == 1 // 不支持联合主键
                && !setId.equals(method.getName())
                && primaryKeyColumns.contains(introspectedColumn)) {
            Method idMethod = new Method();
            idMethod.setVisibility(JavaVisibility.PUBLIC);
            idMethod.setReturnType(method.getReturnType());
            idMethod.setName(setId);
            idMethod.getParameters().addAll(method.getParameters());
            String sb = "this." +
                    method.getName() +
                    '(' +
                    introspectedColumn.getJavaProperty() +
                    ");";
            idMethod.addBodyLine(sb);
            topLevelClass.addMethod(idMethod);
        }
        return true;
    }

    private static void addClass(String clazz, IntrospectedTable introspectedTable,
                                   Consumer<FullyQualifiedJavaType> addImportedType,
                                   Consumer<FullyQualifiedJavaType> addSuper) {
        if (clazz != null && !clazz.isEmpty()) {

            String[] itfs = clazz.split(";\\s*");
            for (String itf : itfs) {

                List<String> types = null;
                int i = itf.indexOf('#');
                if (i > 0 && i < itf.length()) {
                    types = Arrays.asList(itf.substring(i + 1).split("#"));
                    if (types.contains("K")
                            && introspectedTable.getPrimaryKeyColumns().size() > 1) {
                        continue; // 不支持联合主键
                    }
                    itf = itf.substring(0, i);
                }

                FullyQualifiedJavaType importType = new FullyQualifiedJavaType(itf);
                FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(importType.getShortName());
                if (types != null) {
                    for (String type : types) {
                        if ("M".equals(type)) { // 添加model类型到泛型参数中
                            fqjt.addTypeArgument(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));

                        } else if ("K".equals(type)) { // 添加表主键类型到泛型参数中
                            for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
                                addImportedType.accept(column.getFullyQualifiedJavaType());
                                fqjt.addTypeArgument(column.getFullyQualifiedJavaType());
                            }
                        }
                    }
                }

                addImportedType.accept(importType);
                addSuper.accept(fqjt);
            }

        }
    }

    private void addDSLWhereSelective(IntrospectedTable introspectedTable, FullyQualifiedJavaType modelType,
                                      Method method, String dslType, String simpleReturnType) {
        method.setDefault(true);
        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, new HashSet<>());
        method.setName("applyWhereSelective");
        method.getTypeParameters().add(new TypeParameter("R"));
        method.getParameters().add(new Parameter(new FullyQualifiedJavaType(dslType), "dsl"));
        method.getParameters().add(new Parameter(modelType, "params_"));
        boolean first = false;
        if (simpleReturnType != null) {
            first = true; // not AbstractWhereDSL
            int i = dslType.indexOf('<');
            i = i > 0 && i < dslType.length() ? i : dslType.length();
            // QueryExpressionDSL.QueryExpressionWhereBuilder
            FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(dslType.substring(0, i) + "." + simpleReturnType) {
                @Override
                public String getFullyQualifiedName() {
                    // QueryExpressionDSL<R>.QueryExpressionWhereBuilder
                    return dslType + "." + simpleReturnType;
                }
            };
            method.setReturnType(returnType);
        }
        method.addBodyLine("return dsl");
        List<IntrospectedColumn> columns = ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        Iterator<IntrospectedColumn> iter = columns.iterator();
        while (iter.hasNext()) {
            IntrospectedColumn column = iter.next();
            String methodName = JavaBeansUtil.getGetterMethodName(column.getJavaProperty(), column.getFullyQualifiedJavaType());
            String line = "        ." +
                    (first ? "where" : "and") +
                    "(" + column.getJavaProperty() +
                    ", isEqualToWhenPresent(params_::" +
                    methodName + "))";
            if (!iter.hasNext()) {
                line += ";";
            }
            first = false;
            method.addBodyLine(line);
        }
    }

}
