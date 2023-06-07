package com.example.sicupi.ui.main.pimpinan.cuti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sicupi.data.api.ApiConfig;
import com.example.sicupi.data.api.PimpinanService;
import com.example.sicupi.data.model.CutiModel;
import com.example.sicupi.databinding.FragmentPimpinanHistoryCutiMelahirkanBinding;
import com.example.sicupi.databinding.FragmentPimpinanHistoryCutiPentingBinding;
import com.example.sicupi.ui.main.pimpinan.adapter.HistoryAllCutiAdapter;
import com.example.sicupi.util.Constants;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PimpinanHistoryCutiPentingFragment extends Fragment {

    private FragmentPimpinanHistoryCutiPentingBinding binding;
    SharedPreferences sharedPreferences;
    private AlertDialog progressDialog;
    String userId;
    HistoryAllCutiAdapter historyAllCutiAdapter;
    List<CutiModel> cutiModelList;
    LinearLayoutManager linearLayoutManager;
    PimpinanService pimpinanService;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPimpinanHistoryCutiPentingBinding.inflate(inflater, container, false);
        sharedPreferences = getContext().getSharedPreferences(Constants.SHAREDPREFNAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.SHAREDPRE_USER_ID, null);
        pimpinanService = ApiConfig.getClient().create(PimpinanService.class);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData("Cuti Alasan Penting");
        listener();




    }
    private void getData(String keterangan) {
        showProgressBar("Loading", "Memuat data...", true);
        pimpinanService.getAllPengajuanCutiByKeterangan(keterangan).enqueue(new Callback<List<CutiModel>>() {
            @Override
            public void onResponse(Call<List<CutiModel>> call, Response<List<CutiModel>> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    showProgressBar("Loading", "Memuat data...", false);
                    cutiModelList = response.body();
                    historyAllCutiAdapter = new HistoryAllCutiAdapter(getContext(), cutiModelList);
                    linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    binding.rvCuti.setLayoutManager(linearLayoutManager);
                    binding.rvCuti.setAdapter(historyAllCutiAdapter);
                    binding.rvCuti.setHasFixedSize(true);
                    binding.tvEmpty.setVisibility(View.GONE);


                }else {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    showProgressBar("Loading", "Memuat data...", false);


                }
            }

            @Override
            public void onFailure(Call<List<CutiModel>> call, Throwable t) {
                binding.tvEmpty.setVisibility(View.GONE);
                showProgressBar("Loading", "Memuat data...", false);
                showToast("gagal", "Tidak ada koneksi internet");
            }
        });

    }

    private void listener() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });




    }



    private void showProgressBar(String title, String message, boolean isLoading) {
        if (isLoading) {
            // Membuat progress dialog baru jika belum ada
            if (progressDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setCancelable(false);
                progressDialog = builder.create();
            }
            progressDialog.show(); // Menampilkan progress dialog
        } else {
            // Menyembunyikan progress dialog jika ada
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
        private void showToast(String jenis, String text) {
        if (jenis.equals("success")) {
            Toasty.success(getContext(), text, Toasty.LENGTH_SHORT).show();
        }else {
            Toasty.error(getContext(), text, Toasty.LENGTH_SHORT).show();
        }
   }

   private void filter(String keyWord) {
       ArrayList<CutiModel> filteredList = new ArrayList<>();
       for (CutiModel item : cutiModelList) {
           if (item.getNama().toLowerCase().contains(keyWord.toLowerCase())) {
               filteredList.add(item);
           }
       }
       historyAllCutiAdapter.filter(filteredList);
       if (filteredList.isEmpty()) {

       }else {
           historyAllCutiAdapter.filter(filteredList);
       }


   }

}