package androidx.databinding;

public class DataBinderMapperImpl extends MergedDataBinderMapper {
  DataBinderMapperImpl() {
    addMapper(new es.jcyl.ita.frmdrd.DataBinderMapperImpl());
  }
}
