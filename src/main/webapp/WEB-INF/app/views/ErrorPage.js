import React from "react";
import Button from "app/components/Button";
import Card from "app/components/Card";
import { useConfig } from "app/hooks/useConfig";
import {
  ExclamationTriangleIcon,
  PhoneIcon,
} from "@heroicons/react/24/outline";
import { useNavigate } from "react-router-dom";

export default function ErrorPage({ error, resetErrorBoundary }) {
  const navigate = useNavigate();
  const { data: config } = useConfig();
  console.error(error);

  return (
    <div className="flex w-full justify-center px-4 py-12">
      <Card className="w-full max-w-2xl border-t-4 border-teal-600">
        <Card.Content className="p-8 sm:p-10">
          <div className="flex flex-col items-center text-center">
            <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-red-100">
              <ExclamationTriangleIcon
                aria-hidden="true"
                className="h-8 w-8 text-red-600"
              />
            </div>

            <h1 className="text-3xl font-semibold text-teal-700">
              Something went wrong
            </h1>
            <p className="mt-3 max-w-lg text-gray-700">
              We couldn&apos;t complete your request. Please try again, or log
              out and sign back in if the problem continues.
            </p>

            {error?.message && (
              <div
                className="mt-5 w-full border border-red-200 bg-red-50 px-4 py-3 text-left text-sm text-red-800"
                role="alert"
              >
                <span className="font-semibold">Error details: </span>
                <span className="break-words">{error.message}</span>
              </div>
            )}

            <div className="mt-6 flex flex-wrap justify-center gap-3">
              <Button variant="primary" onPress={() => navigate(0)}>
                Try Again
              </Button>
              <Button
                variant="secondary"
                onPress={() => {
                  resetErrorBoundary?.();
                  navigate("/logout");
                }}
              >
                Logout
              </Button>
            </div>
          </div>

          <div className="mt-8 border-t border-gray-200 pt-6">
            <div className="flex items-start gap-3 bg-gray-50 px-4 py-4 text-gray-700">
              <PhoneIcon
                aria-hidden="true"
                className="mt-0.5 h-5 w-5 shrink-0 text-teal-700"
              />
              <div>
                <p className="font-semibold text-teal-700">Need assistance?</p>
                <p className="mt-1 text-sm">
                  If this error continues, contact the STS Help Line
                  {config?.helplinePhoneNumber && (
                    <>
                      {" "}
                      at{" "}
                      <span className="font-semibold text-gray-900">
                        {config.helplinePhoneNumber}
                      </span>
                    </>
                  )}
                  .
                </p>
              </div>
            </div>
          </div>
        </Card.Content>
      </Card>
    </div>
  );
}
