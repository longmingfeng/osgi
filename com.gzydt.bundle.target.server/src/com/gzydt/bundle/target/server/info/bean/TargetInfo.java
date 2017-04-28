package com.gzydt.bundle.target.server.info.bean;

public interface TargetInfo {
    /**
     * 注册target节点
     * 
     * @param target
     *            节点数据
     */
    public void register(Target target);

    /**
     * 获取全部节点
     * 
     * @return 全部target节点
     */
    public Targets getAll();

    /**
     * 根据id查找target节点
     * 
     * @param targetId
     *            节点id
     * @return 节点
     */
    public Target findTargetById(String targetId);

    /**
     * 根据id删除target节点
     * 
     * @param targetId
     *            节点id
     */
    public void removeTargetById(String targetId);
}
